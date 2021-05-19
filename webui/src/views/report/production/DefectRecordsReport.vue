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
          <vxe-form-item span="5" title="开始时间" field="startTime">
            <vxe-input v-model="form.startTime" placeholder="时间选择" type="datetime"></vxe-input>
          </vxe-form-item>
          <vxe-form-item span="5" title="结束时间" field="endTime">
            <vxe-input v-model="form.endTime" placeholder="时间选择" type="datetime"></vxe-input>
          </vxe-form-item>
          <vxe-form-item span="5" title="产品ID" field="productID">
            <vxe-input v-model="form.product" placeholder="请输入产品ID" type="text"></vxe-input>
          </vxe-form-item>
          <vxe-form-item span="5" title="站点" field="operation">
            <query-select
              v-model="form.operation"
              url="/common/executeSql"
              method="post"
              :params="{sql_name: 'getAllOperations'}"
              :option-config="{label: 'description', value: 'name'}"
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
    <a-row :gutter="6">
      <a-col :span='12'>
        <a-card title='不良趋势'>
          <div :style="{height: (height * 0.5)+'px'}">
            <a-row>
              <a-col :span='12'>
                <a-radio-group :value='switchWIPTimeUnit' @change='handlerTimeUnitChange'>
                  <a-radio-button value='%Y-%m-%d %H'>时</a-radio-button>
                  <a-radio-button value='%Y-%m-%d'>日</a-radio-button>
                </a-radio-group>
              </a-col>
            </a-row>
            <line-chart class='cold-capacity-chart' id='WIPTrendLine' :show-split-line='true'
                        :x-axis='waterUseChartLegend'
                        :series-data='waterUseChartSeries' :y-axis='yAxis' />
          </div>
        </a-card>
      </a-col>
      <a-col :span='12'>
        <a-card title='各不良类型占比'>
          <div :style="{height: (height * 0.5)+'px'}">
            <pie-chart id='WIPPie' :datasource='PieDatasource' :legend-list='PieLegend' :total-value='WIPTotalValue' unit='片' text='总产品数'></pie-chart>
          </div>
        </a-card>
      </a-col>
    </a-row>
    <a-spin :spinning="loading" tip="查询中...">
      <data-table
        title='产品信息表'
        :height='height'
        :columns='columns'
        :datasource='datasource'
      />
    </a-spin>
  </div>
</template>

<script>
import QuerySelect from "@comp/QuerySelect";
import LineChart from "@comp/chart/LineChart";
import DataTable from '@comp/DataTable'
import PieChart from '@comp/chart/PieChart'
import { postAction } from '@api/manage'

export default {
  name: "DefectRecordsReport",
  components: {
    DataTable,
    LineChart,
    QuerySelect,
    PieChart
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
        product: '',
        operation: ''
      },
      formRules: {
        'startTime': [
          {required: true}
        ],
        'endTime': [
          {required: true}
        ]
      },
      switchWIPTimeUnit: '%Y-%m-%d',
      loading: false,
      macDurationXAxis: [],
      waterUseChartSeries: [],
      COPChartSeries: [],
      elcChartSeries: [],
      yAxis: [
        {
          name: '%',
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
      columns: [
        {title: '产品 ID', field: 'name', align: 'center', sortable: true},
        {title: '工单号', field: 'product_request_name', align: 'center'},
        {title: '不良代码', field: 'fg_code', align: 'center'},
        {title: '不良描述', field: 'defect_description', align: 'center'},
        {title: '等级', field: 'grade', align: 'center'},
        {title: '最新事件', field: 'last_event_name', align: 'center'},
        {title: '事件发生时间', field: 'last_event_time', align: 'center'},
        {title: '所在站点', field: 'process_operation_name', align: 'center', sortable: true},
        {title: '站点名称', field: 'prc_desc', align: 'center'},
        {title: '操作员 No', field: 'last_event_user', align: 'center'}
      ],
      datasource: [],
      PieDatasource: [],
      PieLegend: [],
      WIPTotalValue: 0
    }
  },
  methods: {
    handlerSubmit() {
      this.handlerWaterUseChartSubmit()
    },
    handlerWaterUseChartSubmit() {
      let _this = this
      let params = _this.form
      this.loading = true

      params['sql_name'] = 'getAllProductInfos'
      postAction('/common/executeSql', params).then(res => {
        _this.datasource = res['result']
      }).catch(error => {
        this.$message.error(error)
      })

      this.PieDatasource = [
        {value: 335, name: 'E101'},
        {value: 310, name: 'E102'},
        {value: 234, name: 'E103'}
      ]
      this.PieLegend = ['E101', 'E102', 'E103']
      this.waterUseChartLegend = ['2021-04-15', '2021-04-16', '2021-04-17', '2021-04-18', '2021-04-19', '2021-04-20', '2021-04-21', '2021-04-22', '2021-04-23']
      this.waterUseChartSeries = [
        { name: '不良率', data: [12, 13, 15, 10, 14, 12, 24, 20, 22, 23] }
      ]
      this.WIPTotalValue = 66

      this.loading = false
    },
    handlerTimeUnitChange() {
      console.log('OK')
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