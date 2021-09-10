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
              <vxe-input v-model="form.startTime" placeholder="日期选择" type="date"></vxe-input>
            </template>
          </vxe-form-item>
          <vxe-form-item span="5" title="结束时间" field="endTime" :item-render="{}">
            <template #default>
              <vxe-input v-model="form.endTime" placeholder="日期选择" type="date"></vxe-input>
            </template>
          </vxe-form-item>
          <vxe-form-item span="5" title="拉线" field="line">
            <query-select
              ref="xLine"
              v-model="form.line"
              :options="lineOptions"
              :option-config="{label: 'description', value: 'name'}"
              @change="handlerLineChange"
            />
          </vxe-form-item>
          <vxe-form-item span="5" title="设备" field="operation">
            <query-select
              ref="xOperation"
              v-model="form.operation"
              :options="operationOptions"
              :option-config="{label: 'mdescription', value: 'mname'}"
            />
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
      <a-card title="设备产能趋势">
        <div :style="{height: (height * 0.5)+'px'}">
          <a-row>
            <a-col :span="12">
              <a-radio-group :value="switchCapacityMacTrendUnit" @change="handlerCapacityMacTUChange">
                <a-radio-button value="YYYY-MM-DD HH24">时</a-radio-button>
                <a-radio-button value="YYYY-MM-DD">日</a-radio-button>
                <a-radio-button value="YYYY-MM">月</a-radio-button>
              </a-radio-group>
            </a-col>
          </a-row>
          <line-chart class="cold-capacity-chart" id="trendWaterUse" :show-split-line="true"
                      :x-axis="capacityMacTLegend" :data-zoom-enable="true"
                      :series-data="capacityMacTSeries" :y-axis="yAxis"
                      :axis-label-interval="null" axis-label-rotate="0"
          />
        </div>
      </a-card>
      <a-card title="AB 班组产能对比" style="width: 100%; display: inline-block; margin-right: 8px">
        <div :style="{height: (height * 0.5)+'px'}">
          <a-row>
            <a-col :span="24">
              <a-radio-group :value="switchCapacityGroupUnit" @change="handlerCapacityGroupUnitChange">
                <a-radio-button value="YYYY-MM-DD HH24">时</a-radio-button>
                <a-radio-button value="YYYY-MM-DD">日</a-radio-button>
                <a-radio-button value="YYYY-MM">月</a-radio-button>
              </a-radio-group>
            </a-col>
          </a-row>
          <line-chart class="cop-chart" id="COPReport" :show-split-line="true" :x-axis="CapacityGroupLegend"
                      :series-data="CapacityGroupSeries" :y-axis="yAxis" :data-zoom-enable="true"
                      :axis-label-interval="1" axis-label-rotate="0"
          />
        </div>
      </a-card>
    </a-spin>
  </div>
</template>

<script>
import BarChart from '@comp/chart/BarChart'
import QuerySelect from '@comp/QuerySelect'
import LineChart from '@comp/chart/LineChart'
import { cloneObject, transferStringToArray } from '@/utils/util'
import { executeSQL } from '@api/api'
import { postAction } from '@api/manage'

export default {
  name: 'CapacityReport',
  components: {
    LineChart,
    QuerySelect,
    BarChart
  },
  computed: {
    height() {
      return this.$store.getters.bodyHeight - 135
    }
  },
  data() {
    return {
      form: {
        startTime: '',
        endTime: '',
        line: '',
        operation: '',
        spec: ''
      },
      formRules: {
        startTime: [
          { required: true, message: '请输入起始时间' }
        ],
        endTime: [
          { required: true, message: '请输入起始时间' }
        ],
        line: [
          { required: true, message: '请输入Line' }
        ]
      },
      switchCapacityMacTrendUnit: 'YYYY-MM-DD',
      switchCapacityGroupUnit: 'YYYY-MM-DD',
      switchElcUnit: 'YYYY-MM-DD',
      loading: false,
      macDurationXAxis: [],
      capacityMacTSeries: [],
      CapacityGroupSeries: [],
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
      capacityMacTLegend: [],
      CapacityGroupLegend: [],
      elcChartLegend: [],
      lineOptions: { options: [], source: [] },
      operationOptions: { options: [] }
    }
  },
  mounted() {
    this.initData()
  },
  methods: {
    initData() {
      let _this = this
      executeSQL({ sql_name: 'getLinesAndOperations' }).then(res => {
        if (res && res['code'] === 200) {
          const dis_res = []
          _this.lineOptions.source = res['result']
          _this.lineOptions.source.forEach(r => {
            if(dis_res.filter(d => { return d['name'] === r['name'] }).length === 0 ){
              dis_res.push({name: r['name'], description: r['description']})
            }
          })
          _this.lineOptions['options'] = dis_res
        } else {
          _this.$message.error(res['message'])
        }
      })
    },
    handlerCapacityMacTUChange(e) {
      let that = this
      that.switchCapacityMacTrendUnit = e.target.value
      if (that.form.startTime && this.form.endTime) {
        that.handlerMacCapacitySubmit(that)
      }
    },
    handlerCapacityGroupUnitChange(e) {
      let that = this
      that.switchCapacityGroupUnit = e.target.value
      if (that.form.startTime && that.form.endTime) {
        that.handlerGroupCapacitySubmit(that)
      }
    },
    handlerSubmit() {
      let that = this
      this.handlerMacCapacitySubmit(that)
      this.handlerGroupCapacitySubmit(that)
    },
    async handlerGroupCapacitySubmit(_this) {
      _this.loading = true
      let series = []
      const params = cloneObject(_this.form)

      params['sql_name'] = 'getCapacityGroupTrend'
      params['unit'] = _this.switchCapacityGroupUnit
      const returnV = await executeSQL(params)
      const res = returnV['result']
      _this.CapacityGroupLegend = transferStringToArray(res[0]['datehour'])
      for (let resKey of res) {
        let sery = {name: resKey['value'], data: transferStringToArray(resKey['count'])}
        series.push(sery)
      }
      _this.CapacityGroupSeries = series

      _this.loading = false
    },
    async handlerMacCapacitySubmit(_this){
      _this.loading = true
      const series = []
      const params = cloneObject(_this.form)

      params['sql_name'] = 'getCapacityTrend'
      params['unit'] = _this.switchCapacityMacTrendUnit
      let returnV = await executeSQL(params)
      let res = returnV['result']
      _this.capacityMacTLegend = transferStringToArray(res[0]['date'])
      for (let resKey of res) {
        let sery = {name: resKey['operation'], data: transferStringToArray(resKey['count'])}
        series.push(sery)
      }
      _this.capacityMacTSeries = series

      _this.loading = false
    },
    handlerReset() {
      this.$refs.xLine.clear()
      this.$refs.xOperation.clear()
      this.$refs.xSpec.clear()
    },
    handlerLineChange(data) {
      let list = this.lineOptions['source'].filter((l) => {
        return l['name'] === data
      })
      if (list.length > 0) {
        this.operationOptions['options'] = list
      }
      this.$refs.xOperation.clear()
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