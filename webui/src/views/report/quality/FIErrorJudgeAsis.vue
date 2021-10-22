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
              <vxe-input v-model="form.startTime" placeholder="时间选择" type="datetime"></vxe-input>
            </template>
          </vxe-form-item>
          <vxe-form-item span="5" title="结束时间" field="endTime" :item-render="{}">
            <template #default>
              <vxe-input v-model="form.endTime" placeholder="时间选择" type="datetime"></vxe-input>
            </template>
          </vxe-form-item>
          <vxe-form-item span="5" title="产品ID" field="product" :item-render="{}">
            <template #default>
              <vxe-input v-model="form.product" placeholder="请输入产品ID" type="text"></vxe-input>
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
    <a-row :gutter="6">
      <a-col :span="12">
        <a-card title="人员漏检率图表">
          <div :style="{height: (height * 0.5 - 8)+'px'}">
            <line-chart class="cold-capacity-chart" id="WIPTrendLine" :show-split-line="true"
                        :x-axis="WIPLineChartLegend"
                        :series-data="WIPChartSeries" :y-axis="yAxis"
                        :axis-label-interval="null" :axis-label-rotate="45"
            />
          </div>
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card title="各不良类型占比">
          <div :style="{height: (height * 0.5)+'px'}">
            <pie-chart id="WIPPie" :datasource="PieDatasource" unit="片" text="总产品数"></pie-chart>
          </div>
        </a-card>
      </a-col>
    </a-row>
    <a-row :gutter="6">
      <a-col :span="7">
        <a-spin :spinning="loading" tip="查询中...">
          <data-table
            title="人员漏检分析"
            :height="height"
            :columns="ErrorJudgeColumns"
            :datasource="ErrorJudgeDatasource"
            :is-hide-pager="true"
            @cell-click="handlerRowClick"
          />
        </a-spin>
      </a-col>
      <a-col :span="17">
        <a-spin :spinning="loading" tip="查询中...">
          <data-table
            title="漏检产品信息表"
            :height="height"
            :columns="columns"
            :datasource="datasource"
            :page-size="20"
            :can-export="true"
          />
        </a-spin>
      </a-col>
    </a-row>

  </div>
</template>

<script>
import QuerySelect from '@comp/QuerySelect'
import LineChart from '@comp/chart/LineChart'
import DataTable from '@comp/DataTable'
import PieChart from '@comp/chart/PieChart'
import SearchSelect from '@comp/SearchSelect'
import { postAction } from '@api/manage'
import { getObjArrayFieldToArray } from '@/utils/util'
const { getCurrentTime } = require('@/utils/util')
import { executeSQL } from '@api/api'

export default {
  name: 'FIErrorJudgeAsis',
  components: {
    SearchSelect,
    DataTable,
    LineChart,
    QuerySelect,
    PieChart
  },
  computed: {
    height() {
      return this.$store.getters.bodyHeight - 190
    }
  },
  mounted() {
    this.initData()
  },
  data() {
    return {
      form: {
        startTime: getCurrentTime('date', -1, 'day') + ' 08:30:00',
        endTime: getCurrentTime('date') + ' 08:30:00',
        product: '',
        product_flag: ''
      },
      formRules: {
        'startTime': [
          { required: true }
        ],
        'endTime': [
          { required: true }
        ]
      },
      switchWIPTimeUnit: 'YYYY-MM-DD',
      loading: false,
      macDurationXAxis: [],
      WIPChartSeries: [],
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
        },{
          name: '片',
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
            show: false
          }
        }
      ],
      WIPLineChartLegend: [],
      COPChartLegend: [],
      elcChartLegend: [],
      switchArea: 'T',
      columns: [
        { title: '产品 ID', field: this.changeUL('name'), align: 'center', sortable: true, width: 200 },
        { title: '工单号', field: this.changeUL('product_request_name'), align: 'center', width: 200  },
        { title: 'FI检查时间', field: this.changeUL('LAST_NEW_TIME'), align: 'center', width: 200  },
        { title: '不良代码', field: this.changeUL('fg_code'), align: 'center', width: 150  },
        { title: '不良描述', field: this.changeUL('defect_description'), align: 'center', width: 150  },
        { title: '等级', field: this.changeUL('grade'), align: 'center', width: 150  },
        { title: '操作员 No', field: this.changeUL('OPERATOR'), align: 'center'  }
      ],
      ErrorJudgeColumns: [
        { title: '人员', field: this.changeUL('USERNAME'), align: 'center', width: 100 },
        { title: '工号', field: this.changeUL('LAST_EVENT_USER'), align: 'center' },
        { title: '判定数量', field: this.changeUL('JUDGE_COUNT'), align: 'center' },
        { title: '漏检数量', field: this.changeUL('ERROR_COUNT'), align: 'center' },
        { title: '漏检率', field: this.changeUL('RATE'), align: 'center' }
      ],
      datasource_source: [],
      datasource: [],
      ErrorJudgeDatasource: [],
      PieDatasource: []
    }
  },
  methods: {
    initData() {
      let _this = this
      // 待操作
    },
    handlerSubmit() {
      this.handleSubmit()
    },
    async handleSubmit() {
      this.loading = true
      this.datasource = []

      let params = JSON.parse(JSON.stringify(this.form))
      let product_str = ""
      const products = params['product'].split(' ')
      for (let i = 0; i < products.length; i++) {
        const product = products[i]
        if(i > 0){
          product_str += "'"
        }
        product_str += product
        if(i < products.length - 1){
          product_str += "',"
        }
      }
      params['product'] = product_str
      if(product_str !== ''){
        params['product_flag'] = 'Y'
      }else{
        params['product_flag'] = ''
      }

      params['sql_name'] = 'getFIErrorJudgeAsis'
      const table_gt_res = await postAction('/common/executeSql', params)
      this.ErrorJudgeDatasource = table_gt_res['result']

      // params['sql_name'] = 'getTrendOfDefectRate'
      // params['unit'] = this.switchWIPTimeUnit
      // const line_result = await postAction('/common/executeSql', params)
      // const line_data = line_result['result']

      this.WIPLineChartLegend = getObjArrayFieldToArray(this.ErrorJudgeDatasource, this.changeUL('USERNAME'))
      this.WIPChartSeries = [
        { name: '漏检率', data: getObjArrayFieldToArray(this.ErrorJudgeDatasource, this.changeUL('RATE')) },
        { name: '漏检数量', data: getObjArrayFieldToArray(this.ErrorJudgeDatasource, this.changeUL('ERROR_COUNT')), type: 'bar', stack: 'total', yIndex: '1' },
        { name: '正确数量', data: getObjArrayFieldToArray(this.ErrorJudgeDatasource, this.changeUL('CONRRECT_COUNT')), type: 'bar', stack: 'total', yIndex: '1' }
      ]

      params['sql_name'] = 'getFIFGCodeRate'
      const pie_data = await postAction('/common/executeSql', params)
      this.PieDatasource = pie_data['result']

      this.loading = false
    },
    handlerRowClick(row){
      let params = this.form
      params['sql_name'] = 'getErrorProductByUser'
      params['operator'] = row['LAST_EVENT_USER']
      postAction('/common/executeSql', params).then(res => {
        if(res['success']) {
          this.datasource = res['result']
        }
      })
    },
    changeUL(str) {
      return this.changeUpperOrLower(str)
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