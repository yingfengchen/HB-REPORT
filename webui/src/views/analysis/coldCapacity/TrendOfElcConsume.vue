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
            <vxe-input v-model="form.startTime" placeholder="日期选择" type="date"></vxe-input>
          </vxe-form-item>
          <vxe-form-item span="5" title="结束时间" field="endTime">
            <vxe-input v-model="form.endTime" placeholder="日期选择" type="date"></vxe-input>
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
      <a-card title="低冷系统工作状态趋势" style="width: calc(50% - 4px); display: inline-block; margin-right: 8px">
        <div :style="{height: (height * 0.5)+'px'}">
          <a-row>
            <a-col :span="12">
              <a-radio-group :value="switchColdUnit" @change="handlerColdUnitChange">
                <a-radio-button value="%Y-%m-%d %H">时</a-radio-button>
                <a-radio-button value="%Y-%m-%d">日</a-radio-button>
                <a-radio-button value="%Y-%m">月</a-radio-button>
              </a-radio-group>
            </a-col>
          </a-row>
          <line-chart class="cop-chart" id="ColdElcReport" :show-split-line="true" :x-axis="ColdChartXAxis"
                      :series-data="ColdChartSeries" :y-axis="yAxis" :boundary-gap="true" />
        </div>
      </a-card>
      <a-card title="中冷系统工作状态趋势" style="width: calc(50% - 4px); display: inline-block">
        <div :style="{height: (height * 0.5)+'px'}">
          <a-row>
            <a-col :span="12">
              <a-radio-group :value="switchWarmUnit" @change="handlerWarmUnitChange">
                <a-radio-button value="%Y-%m-%d %H">时</a-radio-button>
                <a-radio-button value="%Y-%m-%d">日</a-radio-button>
                <a-radio-button value="%Y-%m">月</a-radio-button>
              </a-radio-group>
            </a-col>
          </a-row>
          <line-chart class="cop-chart" id="WarmElcReport" :show-split-line="true" :x-axis="warmChartXAxis"
                      :series-data="warmChartSeries" :y-axis="yAxis" :boundary-gap="true" />
        </div>
      </a-card>
      <a-card title="热水系统工作状态趋势" style="width: calc(50% - 4px); display: inline-block; margin-right: 8px">
        <div :style="{height: (height * 0.5)+'px'}">
          <a-row>
            <a-col :span="12">
              <a-radio-group :value="switchHotUnit" @change="handlerHotUnitChange">
                <a-radio-button value="%Y-%m-%d %H">时</a-radio-button>
                <a-radio-button value="%Y-%m-%d">日</a-radio-button>
                <a-radio-button value="%Y-%m">月</a-radio-button>
              </a-radio-group>
            </a-col>
          </a-row>
          <line-chart class="cold-capacity-chart" :show-split-line="true" id="HotElcReport" :x-axis="hotChartXAxis"
                      :series-data="hotChartSeries" :y-axis="yAxis" :boundary-gap="true" />
        </div>
      </a-card>
    </a-spin>
  </div>
</template>

<script>
import BarChart from '@comp/chart/BarChart'
import QuerySelect from '@comp/QuerySelect'
import LineChart from '@comp/chart/LineChart'
import { getCumulative, getInstantaneous } from '@api/energyApi'
import { transferStringToArray } from '@/utils/util'

export default {
  name: 'TrendOfElcConsume',
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
        endTime: ''
      },
      formRules: {
        startTime: [
          { required: true, message: '请输入起始时间' }
        ],
        endTime: [
          { required: true, message: '请输入起始时间' }
        ]
      },
      switchHotUnit: '%Y-%m-%d',
      switchColdUnit: '%Y-%m-%d',
      switchWarmUnit: '%Y-%m-%d',
      loading: false,
      macDurationXAxis: [],
      hotChartSeries: [],
      ColdChartSeries: [],
      warmChartSeries: [],
      yAxis: [
        {
          name: 'KWh',
          axisLabel: {
            formatter: '{value}',
            color: '#999',
            textStyle: {
              fontSize: 12
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
              fontSize: 12
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
      hotChartXAxis: [],
      ColdChartXAxis: [],
      warmChartXAxis: [],
      switchArea: 'T'
    }
  },
  methods: {
    handlerHotUnitChange(e) {
      let that = this
      this.switchHotUnit = e.target.value
      if (this.form.startTime && this.form.endTime) {
        this.handlerHotChartSubmit()
      }
    },
    handlerColdUnitChange(e) {
      let that = this
      this.switchColdUnit = e.target.value
      if (this.form.startTime && this.form.endTime) {
        this.handlerColdChartSubmit()
      }
    },
    handlerWarmUnitChange(e) {
      let that = this
      this.switchWarmUnit = e.target.value
      if (this.form.startTime && this.form.endTime) {
        this.handlerWarmChartSubmit()
      }
    },
    handlerSubmit() {
      this.handlerHotChartSubmit()
      this.handlerColdChartSubmit()
      this.handlerWarmChartSubmit()
    },
    handlerHotChartSubmit() {
      const params = JSON.parse(JSON.stringify(this.form))
      const seriesData = []

      this.loading = true
      params['timeUnit'] = this.switchHotUnit
      params['tagList'] = 'PEMS_RCHW_CHPowerT.VAL_Actl,PEMS_RCHW_NCHPowerT.VAL_Actl'
      getCumulative(params).then(res => {
        if (res.length > 0) {
          this.hotChartXAxis = transferStringToArray(res[0]['unit'])
          res.forEach(r => {
            let ser = { name: r['tag_name'], data: transferStringToArray(r['value']), type: 'bar', stack: 'elc' }
            seriesData.push(ser)
          })
          if (res.length === 2) {
            seriesData.push({
              name: '占比',
              data: this.getRateOfTwoTag(transferStringToArray(res[0]['value']), transferStringToArray(res[1]['value'])),
              yIndex: 1
            })
          }
          this.hotChartSeries = seriesData
          this.loading = false
        } else {
          this.loading = false
        }
      })
    },
    handlerColdChartSubmit() {
      const params = JSON.parse(JSON.stringify(this.form))
      const seriesData = []

      this.loading = true
      params['timeUnit'] = this.switchColdUnit
      params['tagList'] = 'PEMS_LCHW_CHPowerT.VAL_Actl,PEMS_LCHW_NCHPowerT.VAL_Actl'
      getCumulative(params).then(res => {
        if (res.length > 0) {
          this.ColdChartXAxis = transferStringToArray(res[0]['unit'])
          res.forEach(r => {
            let ser = { name: r['tag_name'], data: transferStringToArray(r['value']), type: 'bar', stack: 'elc' }
            seriesData.push(ser)
          })
          if (res.length === 2) {
            seriesData.push({
              name: '占比',
              data: this.getRateOfTwoTag(transferStringToArray(res[0]['value']), transferStringToArray(res[1]['value'])),
              yIndex: 1
            })
          }
          this.ColdChartSeries = seriesData
          this.loading = false
        } else {
          this.loading = false
        }
      })
    },
    handlerWarmChartSubmit() {
      const params = JSON.parse(JSON.stringify(this.form))
      const seriesData = []

      this.loading = true
      params['timeUnit'] = this.switchWarmUnit
      params['tagList'] = 'PEMS_MCHW_CHPowerT.VAL_Actl,PEMS_MCHW_NCHPowerT.VAL_Actl'
      getCumulative(params).then(res => {
        if (res.length > 0) {
          this.warmChartXAxis = transferStringToArray(res[0]['unit'])
          res.forEach(r => {
            let ser = { name: r['tag_name'], data: transferStringToArray(r['value']), type: 'bar', stack: 'elc' }
            seriesData.push(ser)
          })
          if (res.length === 2) {
            seriesData.push({
              name: '占比',
              data: this.getRateOfTwoTag(transferStringToArray(res[0]['value']), transferStringToArray(res[1]['value'])),
              yIndex: 1
            })
          }
          this.warmChartSeries = seriesData
          this.loading = false
        } else {
          this.loading = false
        }
      })
    },
    getRateOfTwoTag(oneTag, twoTag) {
      let rate = []
      if (oneTag.length > 0 && twoTag.length > 0) {
        for (let i = 0; i < oneTag.length; i++) {
          let c = Number(oneTag[i])
          let f = Number(twoTag[i])
          if (f !== 0 || c !== 0) {
            rate.push(((c / (c + f)) * 100).toFixed(1))
          } else {
            rate.push(0)
          }
        }
      }
      return rate
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