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
              <vxe-input v-model="form.productID" placeholder="请输入产品ID" type="text" clearable></vxe-input>
            </template>
          </vxe-form-item>
          <vxe-form-item span="5" title="物料ID" field="materialID" :item-render="{}">
            <template #default>
              <vxe-input v-model="form.materialID" placeholder="请输入物料ID" type="text" clearable></vxe-input>
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
import DataTable from '@comp/DataTable'
import { executeSQL } from '@api/api'

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
      columns: [
        { title: 'Product ID', field: 'PRODUCT_NAME', align: 'center', sortable: true },
        { title: '物料ID', field: 'COMPONENT_NAME', align: 'center', sortable: true },
        { title: '物料类型', field: 'COMPONENT_TYPE', align: 'center' },
        { title: '物料批次', field: 'CONSUMABLE_LOT_NAME', align: 'center' },
        { title: '投入时间', field: 'CREATE_TIME', align: 'center' },
        { title: '投入人员', field: 'CREATE_USER', align: 'center' },
        { title: '使用设备', field: 'MACHINE_NAME', align: 'center' },
        { title: '站点', field: 'OPERATION_ID', align: 'center' },
        { title: '使用时间', field: 'LAST_EVENT_TIME', align: 'center' },
        { title: '使用人员', field: 'LAST_EVENT_USER', align: 'center' }
      ],
      datasource: []
    }
  },
  methods: {
    handlerSubmit() {
      this.handlerWaterUseChartSubmit()
    },
    handlerWaterUseChartSubmit() {
      let params = this.form
      params['sql_name'] = 'getMaterialUseRecordings'
      this.loading = true

      executeSQL(params).then((res) => {
        this.datasource = res['result']
        console.log(res)
        this.loading = false
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