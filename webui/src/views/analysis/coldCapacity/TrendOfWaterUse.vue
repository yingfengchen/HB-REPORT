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
      <a-card title="冷热源水量趋势">
        <div :style="{height: (height * 0.5)+'px'}">
          <a-row>
            <a-col :span="12">
              <a-radio-group :value="switchWaterUseUnit" @change="handlerWaterUseUnitChange">
                <a-radio-button value="%Y-%m-%d %H">时</a-radio-button>
                <a-radio-button value="%Y-%m-%d">日</a-radio-button>
                <a-radio-button value="%Y-%m">月</a-radio-button>
              </a-radio-group>
            </a-col>
            <a-col :span="12" style="text-align: right">
              <a-radio-group :value="switchArea" @change="handlerAreaChange">
                <a-radio-button value="T">总量</a-radio-button>
                <a-radio-button value="1">1#</a-radio-button>
                <a-radio-button value="2">2#&3#</a-radio-button>
              </a-radio-group>
            </a-col>
          </a-row>
          <line-chart class="cold-capacity-chart" id="trendWaterUse" :show-split-line="true"
                      :x-axis="waterUseChartLegend"
                      :series-data="waterUseChartSeries" :y-axis="yAxis"/>
        </div>
      </a-card>
      <a-card title="冷热源COP趋势" style="width: calc(50% - 4px); display: inline-block; margin-right: 8px">
        <div :style="{height: (height * 0.5)+'px'}">
          <a-row>
            <a-col :span="12">
              <a-radio-group :value="switchCOPUnit" @change="handlerCOPUnitChange">
                <a-radio-button value="%Y-%m-%d %H">时</a-radio-button>
                <a-radio-button value="%Y-%m-%d">日</a-radio-button>
                <a-radio-button value="%Y-%m">月</a-radio-button>
              </a-radio-group>
            </a-col>
          </a-row>
          <line-chart class="cop-chart" id="COPReport" :show-split-line="true" :x-axis="COPChartLegend"
                      :series-data="COPChartSeries" :y-axis="yAxis"/>
        </div>
      </a-card>
      <a-card title="冷热源电量" style="width: calc(50% - 4px); display: inline-block">
        <div :style="{height: (height * 0.5)+'px'}">
          <a-row>
            <a-col :span="12">
              <a-radio-group :value="switchElcUnit" @change="handlerElcUnitChange">
                <a-radio-button value="%Y-%m-%d %H">时</a-radio-button>
                <a-radio-button value="%Y-%m-%d">日</a-radio-button>
                <a-radio-button value="%Y-%m">月</a-radio-button>
              </a-radio-group>
            </a-col>
          </a-row>
          <line-chart class="cop-chart" id="elcReport" :show-split-line="true" :x-axis="elcChartLegend"
                      :series-data="elcChartSeries" :y-axis="yAxis"/>
        </div>
      </a-card>
    </a-spin>
  </div>
</template>

<script>
import BarChart from "@comp/chart/BarChart";
import QuerySelect from "@comp/QuerySelect";
import LineChart from "@comp/chart/LineChart";
import {getCumulative, getInstantaneous} from "@api/energyApi"
import {transferStringToArray} from "@/utils/util";

export default {
  name: "TrendOfWaterUse",
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
          {required: true, message: '请输入起始时间'}
        ],
        endTime: [
          {required: true, message: '请输入起始时间'}
        ]
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
      areaTagList: 'PEMS_LCHW_ColdCapacityT.VAL_Actl,PEMS_MCHW_ColdCapacityT.VAL_Actl,PEMS_RCHW_ColdCapacityT.VAL_Actl'
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
            this.areaTagList = 'PEMS_LCHW_ColdCapacityT.VAL_Actl,PEMS_MCHW_ColdCapacityT.VAL_Actl,PEMS_RCHW_ColdCapacityT.VAL_Actl';
            break;
          case '1':
            this.areaTagList = 'PEMS_LCHW_ColdCapacity1T.VAL_Actl,PEMS_MCHW_ColdCapacity1T.VAL_Actl,PEMS_RCHW_ColdCapacity1T.VAL_Actl';
            break;
          case '2':
            this.areaTagList = 'PEMS_LCHW_ColdCapacity2T.VAL_Actl,PEMS_MCHW_ColdCapacity2T.VAL_Actl,PEMS_RCHW_ColdCapacity2T.VAL_Actl';
            break;
          default:
            break;
        }
        this.handlerWaterUseChartSubmit()
      }
    },
    handlerSubmit() {
      this.handlerWaterUseChartSubmit()
      this.handlerCOPChartSubmit()
      this.handlerElcChartSubmit()
    },
    handlerWaterUseChartSubmit() {
      const params = JSON.parse(JSON.stringify(this.form))
      const seriesData = []

      this.loading = true
      params['timeUnit'] = this.switchWaterUseUnit
      params['tagList'] = this.areaTagList
      params['diff_flag'] = false
      getCumulative(params).then(res => {
        if(res.length > 0) {
          this.waterUseChartLegend = transferStringToArray(res[0]['unit'])
          res.forEach(r => {
            let ser = {name: r['tag_name'], data: transferStringToArray(r['value'])}
            seriesData.push(ser)
          })
          this.waterUseChartSeries = seriesData
          this.loading = false
        }else{
          this.loading = false
        }
      })
    },
    handlerCOPChartSubmit() {
      const params = JSON.parse(JSON.stringify(this.form))
      const seriesData = []

      this.loading = true
      params['timeUnit'] = this.switchCOPUnit
      params['tagList'] = 'PEMS_LCHW_COP.VAL_Actl,PEMS_MCHW_COP.VAL_Actl,PEMS_RCHW_COP.VAL_Actl'
      params['diff_flag'] = false
      getCumulative(params).then(res => {
        if(res.length > 0) {
          this.COPChartLegend = transferStringToArray(res[0]['unit'])
          res.forEach(r => {
            let ser = {name: r['tag_name'], data: transferStringToArray(r['value'])}
            seriesData.push(ser)
          })
          this.COPChartSeries = seriesData
          this.loading = false
        }else{
          this.loading = false
        }
      })
    },
    handlerElcChartSubmit() {
      const params = JSON.parse(JSON.stringify(this.form))
      const seriesData = []

      this.loading = true
      params['timeUnit'] = this.switchElcUnit
      params['tagList'] = 'PEMS_LCHW_PowerT.VAL_Actl,PEMS_MCHW_PowerT.VAL_Actl,PEMS_RCHW_PowerT.VAL_Actl,PEMS_CH_PowerT.VAL_Actl'
      params['diff_flag'] = true
      getCumulative(params).then(res => {
        if(res.length > 0) {
          this.elcChartLegend = transferStringToArray(res[0]['unit'])
          res.forEach(r => {
            let ser = {name: r['tag_name'], data: transferStringToArray(r['value'])}
            seriesData.push(ser)
          })
          this.elcChartSeries = seriesData
          this.loading = false
        }else{
          this.loading = false
        }
      })
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