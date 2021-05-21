<template>
  <div class="trend-of-water-use-div">
    <a-row class="params-panel">
      <a-col :span="24">
        <vxe-form
          ref="xWaterUseForm"
          title-width="100px"
          :loading="loading"
          :data="form"
          :rules="formRules"
          @submit="handlerSubmit"
          @reset="handlerReset"
        >
          <vxe-form-item span="5" title="Lot ID" field="lot" :item-render="{}">
            <template #default>
              <vxe-input v-model="form.lot" placeholder="请输入" type="text" />
            </template>
          </vxe-form-item>
          <vxe-form-item span="5" title="Product ID" field="product" :item-render="{}">
            <template #default>
              <vxe-input v-model="form.product" placeholder="请输入" type="text"></vxe-input>
            </template>
          </vxe-form-item>
          <vxe-form-item span="5" title="站点" field="operation">
            <query-select
              ref="xOperation"
              v-model="form.operation"
              url="/common/executeSql"
              method="post"
              :params="{sql_name: 'getAllOperations'}"
              :option-config="{label: 'description', value: 'name'}"
            />
          </vxe-form-item>
          <vxe-form-item span="5" title="产品规格" field="spec">
            <query-select
              ref="xSpec"
              v-model="form.spec"
              url="/common/executeSql"
              method="post"
              :params="{sql_name: 'getAllProductSpec'}"
              :option-config="{label: 'name', value: 'value'}"
            />
          </vxe-form-item>
          <vxe-form-item>
            <template #default>
              <vxe-button type="submit" status="primary">查询</vxe-button>
              <vxe-button type="reset">重置</vxe-button>
            </template>
          </vxe-form-item>
        </vxe-form>
      </a-col>
    </a-row>
    <a-spin :spinning="loading" tip="查询中...">
      <a-row>
        <a-col :span="12">
          <a-card title="各站点产品堆积情况">
            <div :style="{height: (height * 0.5)+'px'}">
              <line-chart class="cold-capacity-chart" id="trendWaterUse" :show-split-line="true"
                          :x-axis="waterUseChartLegend"
                          :series-data="waterUseChartSeries" :y-axis="yAxis" />
            </div>
          </a-card>
        </a-col>
        <a-col :span="12">
          <a-card title="各产品规格占比">
            <div :style="{height: (height * 0.5 + 8)+'px'}">
              <pie-chart id="PersonErrorPie" :datasource="PieDatasource" :legend-list="PieLegend" :total-value="879"
                         unit="片" text="总产品数"></pie-chart>
            </div>
          </a-card>
        </a-col>
      </a-row>
      <data-table
        title="在制 Product 列表"
        :height="890"
        :columns="columns"
        :datasource="datasource"
        :page-size="24"
      />
    </a-spin>
  </div>
</template>

<script>
import BarChart from '@comp/chart/BarChart'
import QuerySelect from '@comp/QuerySelect'
import LineChart from '@comp/chart/LineChart'
import { getCumulative, getInstantaneous } from '@api/energyApi'
import { getObjArrayFieldToArray, transferStringToArray } from '@/utils/util'
import DataTable from '@comp/DataTable'
import PieChart from '@comp/chart/PieChart'
import { postAction } from '@api/manage'

export default {
  name: 'WIPSearchReport',
  components: {
    PieChart,
    LineChart,
    QuerySelect,
    BarChart,
    DataTable
  },
  computed: {
    height() {
      return this.$store.getters.bodyHeight - 135
    }
  },
  data() {
    return {
      form: {
        lot: '',
        product: '',
        spec: '',
        operation: ''
      },
      formRules: {},
      switchCOPUnit: '%Y-%m-%d',
      switchElcUnit: '%Y-%m-%d',
      loading: false,
      macDurationXAxis: [],
      waterUseChartSeries: [],
      COPChartSeries: [],
      elcChartSeries: [],
      yAxis: [
        {
          name: '片数',
          axisLabel: {
            formatter: '{value}',
            color: '#999',
            textStyle: {
              fontSize: 13
            }
          },
          axisLine: {
            lineStyle: {
              color: 'rgba(107,107,107,0.37)'
            }
          },
          axisTick: {
            show: false
          },
          splitLine: {
            show: true,
            lineStyle: {
              color: 'rgba(131,101,101,0.2)',
              type: 'dashed'
            }
          }
        }
      ],
      waterUseChartLegend: [],
      COPChartLegend: [],
      elcChartLegend: [],
      switchArea: 'T',
      areaTagList: 'PEMS_LCHW_ColdCapacityT.VAL_Actl,PEMS_MCHW_ColdCapacityT.VAL_Actl,PEMS_RCHW_ColdCapacityT.VAL_Actl',
      columns: [
        { title: '产品ID', field: 'name', align: 'center', sortable: true },
        { title: '所在站点', field: 'process_operation_name', align: 'center' },
        { title: '所属Lot', field: 'lot_name', align: 'center' },
        { title: '所属工单', field: 'product_request_name', align: 'center' },
        { title: '等级', field: 'grade', align: 'center' },
        { title: '不良Code', field: 'fg_code', align: 'center' },
        { title: '是否返工', field: 'rework_state', align: 'center' },
        { title: '上次操作事件', field: 'last_event_name', align: 'center' },
        { title: '上次操作时间', field: 'last_event_time', align: 'center', sortable: true }
      ],
      datasource: [],
      PieDatasource: [],
      PieLegend: []
    }
  },
  mounted() {
    let _this = this
    setTimeout(() => {
      _this.handlerWaterUseChartSubmit()
    }, 300)
  },
  methods: {
    handlerCOPUnitChange(e) {
      let that = this
      this.switchCOPUnit = e.target.value
      if (this.form.startTime && this.form.endTime) {
        this.handlerCOPChartSubmit()
      }
    },
    handlerElcUnitChange(e) {
      let that = this
      this.switchElcUnit = e.target.value
      if (this.form.startTime && this.form.endTime) {
        this.handlerElcChartSubmit()
      }
    },
    handlerAreaChange(e) {
      let that = this
      this.switchArea = e.target.value
      if (this.form.startTime && this.form.endTime) {
        switch (this.switchArea) {
          case 'T':
            this.areaTagList = 'PEMS_LCHW_ColdCapacityT.VAL_Actl,PEMS_MCHW_ColdCapacityT.VAL_Actl,PEMS_RCHW_ColdCapacityT.VAL_Actl'
            break
          case '1':
            this.areaTagList = 'PEMS_LCHW_ColdCapacity1T.VAL_Actl,PEMS_MCHW_ColdCapacity1T.VAL_Actl,PEMS_RCHW_ColdCapacity1T.VAL_Actl'
            break
          case '2':
            this.areaTagList = 'PEMS_LCHW_ColdCapacity2T.VAL_Actl,PEMS_MCHW_ColdCapacity2T.VAL_Actl,PEMS_RCHW_ColdCapacity2T.VAL_Actl'
            break
          default:
            break
        }
        this.handlerWaterUseChartSubmit()
      }
    },
    handlerSubmit() {
      this.handlerWaterUseChartSubmit()
    },
    handlerReset() {
      this.$refs.xOperation.clear()
      this.$refs.xSpec.clear()
    },
    async handlerWaterUseChartSubmit() {
      let params = this.form
      this.loading = true

      params['sql_name'] = 'getWIPOperationCount'
      const line_res = await postAction('/common/executeSql', params)
      this.waterUseChartLegend = getObjArrayFieldToArray(line_res['result'], 'name')
      this.waterUseChartSeries = [
        { name: '当前产品数量', data: getObjArrayFieldToArray(line_res['result'], 'value') }
      ]

      params['sql_name'] = 'getWIPInfoByParams'
      const table_res = await postAction('/common/executeSql', params)
      this.datasource = table_res['result']

      params['sql_name'] = 'getWIPSpecRateByParams'
      const pie_res = await postAction('/common/executeSql', params)
      this.PieDatasource = pie_res['result']

      this.loading = false
    }
  }
}
</script>

<style lang="less" scoped>
.trend-of-water-use-div {
  /deep/ .ant-card {
    margin-bottom: 8px;

    .ant-card-head {
      min-height: 0;

      .ant-card-head-title {
        padding: 8px 0;
      }
    }
  }
}

.params-panel {
  padding: 6px 12px;
  background: #FFFFFF;
  border-radius: 4px;
  margin-bottom: 6px;
}

.cold-capacity-chart {
  margin-top: 8px;
  border-radius: 5px;
  background: #ffffff;
  width: 100%;
  height: calc(100% - 30px);
}

.cop-chart {
  margin-top: 8px;
  border-radius: 5px;
  background: #ffffff;
  width: 100%;
  height: calc(100% - 30px);
}

.gauge-chart-area {
  margin-top: 8px;
  border-radius: 5px;
  width: 100%;
  height: 45%;

  /deep/ .ant-col {
    height: 100%;
    margin-bottom: 10px;
  }
}

.gauge-card {
  background: #ffffff;
  width: 100%;
  height: 100%;

  /deep/ .ant-card-body {
    height: calc(100% - 58px);
  }
}

.gauge-chart {
  background: #ffffff;
  width: 100%;
  height: 100%;
}
</style>