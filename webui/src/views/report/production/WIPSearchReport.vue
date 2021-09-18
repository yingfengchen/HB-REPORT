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
          <vxe-form-item span="5" title="Flow" field="flow">
            <query-select
              ref="xLine"
              v-model="form.flow"
              :options="flowOptions"
              :option-config="{label: changeUL('process_flow_name'), value: changeUL('process_flow_name')}"
              :display-first-default="displayFirst"
              @change="handlerLineChange"
            />
          </vxe-form-item>
          <vxe-form-item span="5" title="站点" field="operation">
            <search-select
              style="width: 100%"
              ref="xOperation"
              v-model="form.operation"
              :options="operationOptions"
              :option-config="{label: 'description', value: 'name'}"
            />
          </vxe-form-item>
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
          <vxe-form-item span="5" title="产品规格" field="spec" folding>
            <query-select
              ref="xSpec"
              v-model="form.spec"
              url="/common/executeSql"
              method="post"
              :params="{sql_name: 'getAllProductSpec'}"
              :option-config="{label: 'name', value: 'value'}"
            />
          </vxe-form-item>
          <vxe-form-item span="5" title="工单" field="wo" folding>
            <search-select
              style="width: 100%"
              ref="xWO"
              v-model="form.wo"
              :options="woOptions"
              :option-config="{label: 'NAME', value: 'NAME'}"
            />
          </vxe-form-item>
          <vxe-form-item collapse-node>
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
          <a-card :title="'各站点产品堆积情况 -- ' + this.form.flow">
            <div :style="{height: (height * 0.5)+'px'}">
              <line-chart class="cold-capacity-chart" id="trendWaterUse" :show-split-line="true"
                          :x-axis="waterUseChartLegend" :boundary-gap="true"
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
        title="在制 Product 列表 (30 天未操作视为滞留)"
        :height="890"
        :columns="columns"
        :datasource="datasource"
        :page-size="24"
        :cell-style="handlerRowStyle"
        :can-export="true"
      />
    </a-spin>
  </div>
</template>

<script>
import BarChart from '@comp/chart/BarChart'
import QuerySelect from '@comp/QuerySelect'
import LineChart from '@comp/chart/LineChart'
import { getObjArrayFieldToArray, getRangeOfTime } from '@/utils/util'
import DataTable from '@comp/DataTable'
import PieChart from '@comp/chart/PieChart'
import { postAction } from '@api/manage'
import { executeSQL } from '@api/api'
import SearchSelect from '@comp/SearchSelect'

export default {
  name: 'WIPSearchReport',
  components: {
    SearchSelect,
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
        flow: '',
        operation: '',
        wo: ''
      },
      formRules: {
        flow: [
          {required: true, message: '请选择工艺Flow'}
        ]
      },
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
      columns: [
        { title: '产品ID', field: this.changeUL('name'), align: 'center', sortable: true },
        { title: '所在站点', field: this.changeUL('process_operation_name'), align: 'center' },
        { title: '所属Lot', field: this.changeUL('lot_name'), align: 'center' },
        { title: '所属工单', field: this.changeUL('product_request_name'), align: 'center' },
        { title: '等级', field: this.changeUL('grade'), align: 'center' },
        { title: '不良Code', field: this.changeUL('fg_code'), align: 'center' },
        { title: '是否返工', field: this.changeUL('rework_state'), align: 'center' },
        { title: '操作事件', field: this.changeUL('last_event_name'), align: 'center' },
        { title: '操作用户', field: this.changeUL('last_event_user'), align: 'center' },
        { title: '操作时间', field: this.changeUL('last_event_time'), align: 'center', sortable: true }
      ],
      datasource: [],
      PieDatasource: [],
      PieLegend: [],
      flowOptions: { options: [] },
      operationOptions: { options: [] },
      woOptions: { options: [] },
      displayFirst: true
    }
  },
  mounted() {
    let _this = this
    _this.initData()
    setTimeout(() => {
      _this.handlerSubmit()
    }, 600)
  },
  methods: {
    initData() {
      let _this = this
      executeSQL({ sql_name: 'getFlowOperations' }).then(res => {
        if (res && res['code'] === 200) {
          _this.flowOptions['options'] = res['result']
          if(_this.displayFirst && _this.flowOptions['options'].length > 0){
            let first = _this.flowOptions['options'][0]
            if (first) {
              _this.operationOptions['options'] = JSON.parse(first[_this.changeUL('operations')])
              _this.form.flow = first[_this.changeUL('process_flow_name')]
            }
          }
        } else {
          _this.$message.error(res['message'])
        }
      })

      executeSQL({sql_name: 'getAllProductRequest'}).then(res => {
        if (res && res['code'] === 200) {
          _this.woOptions['options'] = res['result']
        }else {
          _this.$message.error(res['message'])
        }
      })
    },
    handlerSubmit() {
      this.handlerWIPChartSubmit()
    },
    handlerReset() {
      this.$refs.xLine.clear()
      this.$refs.xOperation.clear()
      this.$refs.xSpec.clear()
      this.$refs.xWO.clear()
    },
    async handlerWIPChartSubmit() {
      let params = this.form
      this.loading = true

      params['sql_name'] = 'getWIPOperationCount'
      const line_res = await postAction('/common/executeSql', params)
      this.waterUseChartLegend = getObjArrayFieldToArray(line_res['result'], this.changeUL('name'))
      this.waterUseChartSeries = [
        { name: '当前产品数量', type: 'bar', data: getObjArrayFieldToArray(line_res['result'], this.changeUL('value')), showLabel: true }
      ]

      params['sql_name'] = 'getWIPSpecRateByParams'
      const pie_res = await postAction('/common/executeSql', params)
      this.PieDatasource = pie_res['result']

      params['sql_name'] = 'getWIPInfoByParams'
      const table_res = await postAction('/common/executeSql', params)
      this.datasource = table_res['result']

      this.loading = false
    },
    handlerLineChange(data) {
      let list = this.flowOptions['options'].filter((l) => {
        return l[this.changeUL('process_flow_name')] === data
      })
      if (list.length > 0) {
        this.operationOptions['options'] = JSON.parse(list[0][this.changeUL('operations')])
      }
      this.$refs.xOperation.clear()
    },
    handlerRowStyle({ row }) {
      if(getRangeOfTime(row[this.changeUL('last_event_time')]) >= 30){
        return {
          color: '#ff3b3b'
        }
      }
    },
    changeUL(str) {
      return this.changeUpperOrLower(str)
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