<template>
  <div class="trend-of-water-use-div">
    <a-row class="params-panel" type="flex" justify="space-around">
      <a-col :span="22">
        <vxe-form
          ref="xWaterUseForm"
          title-width="100px"
          :loading="loading"
          :data="form"
          :rules="formRules"
          @submit="handlerSubmit"
        >
          <vxe-form-item span="5" title="采集时间段" field="startTime" title-overflow="ellipsis">
            <template #default>
              <vxe-input v-model="form.startTime" placeholder="时间选择" type="date" :editable="false" />
            </template>
          </vxe-form-item>
          <vxe-form-item span="4" title="~" title-width="25px" field="endTime" title-overflow="ellipsis">
            <template #default>
              <vxe-input v-model="form.endTime" placeholder="时间选择" type="date" :editable="false" />
            </template>
          </vxe-form-item>
          <vxe-form-item span="5" title="Flow" field="flow">
            <query-select
              ref="xLine"
              v-model="form.flow"
              :options="flowOptions"
              :option-config="{label: 'process_flow_name', value: 'process_flow_name'}"
              :display-first-default="displayFirst"
            />
          </vxe-form-item>
          <vxe-form-item>
            <template #default>
              <vxe-button type="submit" status="primary">查询</vxe-button>
              <vxe-button type="reset" v-if="false">重置</vxe-button>
            </template>
          </vxe-form-item>
        </vxe-form>
      </a-col>
      <a-col :span="2" style="display: flex; justify-content: flex-end; align-items: center;">
        <a-button v-print="'#FPYReport'" icon="printer" />
        <a-button v-if="fpy !== 100" style="margin-left: 10px" icon="calculator" @click="handlerCalculator" />
      </a-col>
    </a-row>
    <div class="info-overview">
      <label>Flow 直通率：<a>{{ fpy }} </a>% [ 直通率计算公式：FPY = p1 * p2 *..., p 为各个检查站点的良率 ]</label>
    </div>
    <a-spin :spinning="loading" tip="查询中..." id="FPYReport">
      <a-row>
        <a-col :span="12">
          <a-card :title="'各站点良品/不良品数以及良率趋势 -- ' + this.form.flow">
            <div :style="{height: (height * 0.5)+'px'}">
              <line-chart
                class="cold-capacity-chart"
                id="trendYield"
                :show-split-line="true"
                :boundary-gap="true"
                :y-axis="yAxis"
                :x-axis="fpyChartXAxis"
                :series-data="yieldChartSeries"
              />
            </div>
          </a-card>
        </a-col>
        <a-col :span="12">
          <a-card :title="'各站点直通率趋势 -- ' + this.form.flow">
            <div :style="{height: (height * 0.5)+'px'}">
              <line-chart
                class="cold-capacity-chart"
                id="trendFPY"
                :show-split-line="true"
                :y-axis="yAxis_1"
                :x-axis="fpyChartXAxis"
                :series-data="fpyChartSeries"
              />
            </div>
          </a-card>
        </a-col>
      </a-row>
      <data-table
        ref="xFPYTable"
        title="详细信息列表"
        :height="600"
        :columns="columns"
        :datasource="datasource"
        :page-size="24"
      />
    </a-spin>
    <FPYCalculatorModal
      :visible="FPYCModalVisible"
      :fpy="fpy"
      @close="() => { this.FPYCModalVisible = false }"
    />
  </div>
</template>

<script>
import BarChart from '@comp/chart/BarChart'
import QuerySelect from '@comp/QuerySelect'
import LineChart from '@comp/chart/LineChart'
import { getCurrentTime } from '@/utils/util'
import DataTable from '@comp/DataTable'
import PieChart from '@comp/chart/PieChart'
import VXETable from 'vxe-table'
import { executeSQL } from '@api/api'
import FPYCalculatorModal from '@views/report/production/modal/FPYCalculatorModal'

export default {
  name: 'FirstPassReport',
  components: {
    FPYCalculatorModal,
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
        flow: '',
        startTime: getCurrentTime('date', -7, 'day'),
        endTime: getCurrentTime('date')
      },
      formRules: {
        flow: [
          { required: true, message: '请选择工艺Flow' }
        ]
      },
      loading: false,
      fpyChartXAxis: [],
      fpyChartSeries: [],
      yieldChartSeries: [],
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
        }, {
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
            show: false,
            lineStyle: {
              color: 'rgba(131,101,101,0.2)',
              type: 'dashed'
            }
          }
        }
      ],
      yAxis_1: [
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
      columns: [
        { title: 'Flow', field: 'processflow_name', align: 'center', sortable: true },
        { title: '站点', field: 'process_operation_name', align: 'center' },
        { title: '良品数', field: 'ok_count', align: 'center' },
        { title: '不良品数', field: 'ng_count', align: 'center' },
        { title: '良率', field: 'yield', align: 'center', formatter: 'formatterFloat' },
        { title: '直通率', field: 'fpy', align: 'center', formatter: 'formatterFloat' }
      ],
      datasource: [],
      flowOptions: { options: [] },
      operationOptions: { options: [] },
      displayFirst: true,
      fpy: 100,
      FPYCModalVisible: false
    }
  },
  created() {
    VXETable.formats.add('formatterFloat', ({ cellValue }) => {
      return (cellValue * 100).toFixed(2) + '%'
    })
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
          let first = _this.flowOptions['options'][0]
          if (first) {
            _this.form.flow = first['process_flow_name']
          }
        } else {
          _this.$message.error(res['message'])
        }
      })
    },
    handlerSubmit() {
      this.handlerWIPChartSubmit()
    },
    async handlerWIPChartSubmit() {
      this.loading = true

      let temp_yield = 1
      let params = this.form
      params['sql_name'] = 'getFirstPassYield'
      let res = await executeSQL(params)
      if (res && res['result']) {
        let sery_temp = [
          { name: '良品数', type: 'bar', yIndex: 0, stack: 'total', data: [] },
          { name: '不良品数', type: 'bar', yIndex: 0, stack: 'total', data: [] },
          { name: '良率', type: 'line', yIndex: 1, data: [], markLine: { data: 90 }}
        ]
        let sery_temp_fpy = [
          { name: '直通率', type: 'line', yIndex: 0, data: [], markLine: { data: 40 }}
        ]
        let result = res['result']
        this.datasource = result
        this.fpyChartXAxis = []
        result.forEach(r => {
          temp_yield *= r['yield']
          r['fpy'] = temp_yield.toFixed(4)
          this.fpyChartXAxis.push(r['process_operation_name'])
          sery_temp[0].data.push(r['ok_count'])
          sery_temp[1].data.push(r['ng_count'])
          sery_temp[2].data.push(r['yield'] * 100)
          sery_temp_fpy[0].data.push(r['fpy'] * 100)
        })
        this.fpy = temp_yield.toFixed(4) * 100
        this.yieldChartSeries = sery_temp
        this.fpyChartSeries = sery_temp_fpy

        this.loading = false
      }
    },
    handlerCalculator() {
      this.FPYCModalVisible = true
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

.info-overview {
  width: 100%;
  height: 39px;
  margin: 10px 0 10px 0;
  background: #e7f4ff;
  border-radius: 6px;
  border: 1px solid #76c4ff;
  padding-left: 15px;
  line-height: 39px;
  font-weight: bolder;
}
</style>