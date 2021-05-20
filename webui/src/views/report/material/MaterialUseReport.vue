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
        >
          <vxe-form-item span="5" title="开始时间" field="startTime" :item-render="{}">
            <template #default>
              <vxe-input v-model="form.startTime" placeholder="日期选择" type="date"/>
            </template>
          </vxe-form-item>
          <vxe-form-item span="5" title="结束时间" field="endTime" :item-render="{}">
            <template #default>
              <vxe-input v-model="form.endTime" placeholder="日期选择" type="date"></vxe-input>
            </template>
          </vxe-form-item>
          <vxe-form-item span="5" title="产品ID" field="productID" :item-render="{}">
            <template #default>
              <vxe-input v-model="form.productID" placeholder="请输入产品ID" type="text"></vxe-input>
            </template>
          </vxe-form-item>
          <vxe-form-item span="5" title="物料ID" field="materialID" :item-render="{}">
            <template #default>
              <vxe-input v-model="form.materialID" placeholder="请输入物料ID" type="text"></vxe-input>
            </template>
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
      <data-table
        title="物料使用记录报表"
        :height="height"
        :columns="columns"
        :datasource="datasource"
      />
    </a-spin>
  </div>
</template>

<script>
import BarChart from '@comp/chart/BarChart'
import QuerySelect from '@comp/QuerySelect'
import LineChart from '@comp/chart/LineChart'
import { getCumulative, getInstantaneous } from '@api/energyApi'
import { transferStringToArray } from '@/utils/util'
import { postAction } from '@api/manage'
import DataTable from '@comp/DataTable'
import Vue from 'vue'

export default {
  name: 'MaterialUseReport',
  components: {
    DataTable,
    LineChart,
    QuerySelect,
    BarChart
  },
  computed: {
    height() {
      return this.$store.getters.bodyHeight - 175
    }
  },
  data() {
    return {
      form: {
        startTime: '',
        endTime: '',
        productID: '',
        materialID: ''
      },
      formRules: {
        'startTime': [{
          required: true
        }],
        'endTime': [{
          required: true
        }]
      },
      switchWaterUseUnit: '%Y-%m-%d',
      switchCOPUnit: '%Y-%m-%d',
      switchElcUnit: '%Y-%m-%d',
      loading: false,
      macDurationXAxis: [],
      waterUseChartSeries: [],
      COPChartSeries: [],
      elcChartSeries: [],
      yAxis: [
        {
          name: 'KWh',
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
        { title: '物料ID', field: 'ID', align: 'center', sortable: true },
        { title: '物料名称', field: 'Name', align: 'center' },
        { title: '供应商', field: 'suplyer', align: 'center' },
        { title: '生产日期', field: 'productionDate', align: 'center' },
        { title: '入厂日期', field: 'inWMSDate', align: 'center' },
        { title: '接收人', field: 'reciver', align: 'center' },
        { title: '绑定产品ID', field: 'productionID', align: 'center', sortable: true },
        { title: '使用时间', field: 'useTime', align: 'center' }
      ],
      datasource: []
    }
  },
  methods: {
    handlerWaterUseUnitChange(e) {
      let that = this
      this.switchWaterUseUnit = e.target.value
      if (this.form.startTime && this.form.endTime) {
        this.handlerWaterUseChartSubmit()
      }
    },
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
    handlerWaterUseChartSubmit() {
      let params = this.form

      params['sql_name'] = 'getMaterialUseRecordings'
      postAction('/common/executeSql', params).then((res) => {
        this.datasource = res['result']
      })
    }
  }
}
</script>

<style lang="less" scoped>
.trend-of-water-use-div {
  margin: -6px -12px 0 0;

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