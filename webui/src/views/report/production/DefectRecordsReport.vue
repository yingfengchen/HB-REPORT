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
          <vxe-form-item span="5" title="站点" field="operation" :item-render="{}">
            <search-select
              style="width: 100%"
              ref="xOperation"
              v-model="form.operation"
              :options="operationOptions"
              :option-config="{label: changeUL('description'), value: changeUL('name')}"
            />
          </vxe-form-item>
          <vxe-form-item span="5" title="不良等级" field="grade" folding>
            <query-select
              ref="xGrade"
              v-model="form.grade"
              url="/common/executeSql"
              method="post"
              :multiple="true"
              :params="{sql_name: 'getAllGradeList'}"
              :option-config="{label: 'name', value: 'name'}"
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
    <a-row :gutter="6">
      <a-col :span="12">
        <a-card title="不良趋势">
          <div :style="{height: (height * 0.5)+'px'}">
            <a-row>
              <a-col :span="12">
                <a-radio-group :value="switchWIPTimeUnit" @change="handlerTimeUnitChange">
                  <a-radio-button value="YYYY-MM-DD HH">时</a-radio-button>
                  <a-radio-button value="YYYY-MM-DD">日</a-radio-button>
                </a-radio-group>
              </a-col>
            </a-row>
            <line-chart class="cold-capacity-chart" id="WIPTrendLine" :show-split-line="true"
                        :x-axis="WIPLineChartLegend"
                        :series-data="WIPChartSeries" :y-axis="yAxis"
                        :axis-label-interval="null" :axis-label-rotate="0.0"
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
      <a-col :span="6">
        <a-spin :spinning="loading" tip="查询中...">
          <data-table
            title="不良分类列表"
            :height="height"
            :columns="defectColumns"
            :datasource="defectDatasource"
            :is-hide-pager="true"
            @cell-click="handlerRowClick"
          />
        </a-spin>
      </a-col>
      <a-col :span="18">
        <a-spin :spinning="loading" tip="查询中...">
          <data-table
            title="产品信息表"
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
import { postAction } from '@api/manage'
import { getObjArrayFieldToArray } from '@/utils/util'
import SearchSelect from '@comp/SearchSelect'
const { getCurrentTime } = require('@/utils/util')
import { executeSQL } from '@api/api'

export default {
  name: 'DefectRecordsReport',
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
        operation: '',
        grade: []
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
        }
      ],
      WIPLineChartLegend: [],
      COPChartLegend: [],
      elcChartLegend: [],
      switchArea: 'T',
      columns: [
        { title: '产品 ID', field: this.changeUL('name'), align: 'center', sortable: true, width: 150 },
        { title: '背光 ID', field: this.changeUL('id1'), align: 'center', width: 200 },
        { title: '工单号', field: this.changeUL('product_request_name'), align: 'center', width: 150  },
        { title: '不良代码', field: this.changeUL('fg_code'), align: 'center', width: 150  },
        { title: '不良描述', field: this.changeUL('defect_description'), align: 'center', width: 150  },
        { title: '等级', field: this.changeUL('grade'), align: 'center', width: 150  },
        { title: '最新事件', field: this.changeUL('last_event_name'), align: 'center', width: 150  },
        { title: '事件发生时间', field: this.changeUL('last_event_time'), align: 'center', width: 150  },
        { title: '所在站点', field: this.changeUL('process_operation_name'), align: 'center', sortable: true, width: 150  },
        { title: '站点名称', field: this.changeUL('prc_desc'), align: 'center', width: 150  },
        { title: '操作员 No', field: this.changeUL('last_event_user'), align: 'center', width: 150  }
      ],
      defectColumns: [
        { title: '不良类别', field: this.changeUL('defect_type'), align: 'center' },
        { title: '不良数量', field: this.changeUL('count'), align: 'center' }
      ],
      datasource_source: [],
      datasource: [],
      defectDatasource: [],
      PieDatasource: [],
      operationOptions: {
        options: []
      }
    }
  },
  methods: {
    initData() {
      let _this = this
      executeSQL({sql_name: 'getAllOperations'}).then(res => {
        if (res && res['code'] === 200) {
          _this.operationOptions['options'] = res['result']
        }else {
          _this.$message.error(res['message'])
        }
      })
    },
    handlerSubmit() {
      this.handlerWaterUseChartSubmit()
    },
    handlerReset() {
      this.$refs.xOperation.clear()
    },
    async handlerWaterUseChartSubmit() {
      this.loading = true

      let params = this.form
      params['grade'] = params['grade'].toString()
      console.log(params)
      params['sql_name'] = 'getAllProdInfosGroupType'
      const table_gt_res = await postAction('/common/executeSql', params)
      this.defectDatasource = table_gt_res['result']

      params['sql_name'] = 'getAllProductInfos'
      const table_res = await postAction('/common/executeSql', params)
      if(table_res && table_res['code'] === 200) {
        this.datasource_source = table_res['result']
        if (this.defectDatasource.length > 0) {
          this.datasource = this.datasource_source.filter((p) => {
            return p['defect_type'] === this.defectDatasource[0]['defect_type']
          })
        } else {
          this.datasource = this.datasource_source
        }
      }

      params['sql_name'] = 'getRateOfDefectCode'
      const pie_data = await postAction('/common/executeSql', params)
      this.PieDatasource = pie_data['result']

      params['sql_name'] = 'getTrendOfDefectRate'
      params['unit'] = this.switchWIPTimeUnit
      const line_result = await postAction('/common/executeSql', params)
      const line_data = line_result['result']

      this.WIPLineChartLegend = getObjArrayFieldToArray(line_data, this.changeUL('datehour'))
      this.WIPChartSeries = [
        { name: '不良率', data: getObjArrayFieldToArray(line_data, this.changeUL('rate')) }
      ]

      this.loading = false
    },
    async handlerTimeUnitChange(e) {
      this.switchWIPTimeUnit = e.target.value

      //render line chart
      let params = this.form
      params['sql_name'] = 'getTrendOfDefectRate'
      params['unit'] = this.switchWIPTimeUnit
      const line_result = await postAction('/common/executeSql', params)
      const line_data = line_result['result']

      this.WIPLineChartLegend = getObjArrayFieldToArray(line_data, this.changeUL('datehour'))
      this.WIPChartSeries = [
        { name: '不良率', data: getObjArrayFieldToArray(line_data, this.changeUL('rate')) }
      ]
    },
    handlerRowClick(row){
      this.datasource = this.datasource_source.filter((p) => {
        return p[this.changeUL('defect_type')] === row[this.changeUL('defect_type')]
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