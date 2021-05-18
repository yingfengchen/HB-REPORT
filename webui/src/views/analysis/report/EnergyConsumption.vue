<template>
  <div class="main-mac-report-div">
    <a-spin :spinning="loading" tip="查询中...">
      <div :style="{height: height+'px'}" class="report-panel">
        <a-row>
          <a-col :span="12">
            <a-radio-group :value="switchUnit" @change="handlerUnitChange">
              <a-radio-button value="day">日</a-radio-button>
              <a-radio-button value="month">月</a-radio-button>
            </a-radio-group>
          </a-col>
          <a-col :span="12" style="text-align: right">
            <a-radio-group :value="regionUnit" @change="handlerRegionUnitChange">
              <a-radio-button value="line">line</a-radio-button>
              <a-radio-button value="area">area</a-radio-button>
            </a-radio-group>
          </a-col>
        </a-row>
        <bar-chart class="mac-opera-report-chart" id="macOperaReport" :show-split-line="true" :legend="barChartLegend"
                   :x-axis="macDurationXAxis" :series-data="series" :is-report="true" :y-axis="yAxis"
        ></bar-chart>
        <a-row class="gauge-chart-area" :gutter="4">
          <a-col :xl="{span: 6}" :lg="{span: 12}" :sm="{span: 24}">
            <a-card :loading="loading" class="gauge-card" title="产品A/单耗">
              <gauge-chart class="gauge-chart" id="ECReportGauge1" :value="energySingle.A"></gauge-chart>
            </a-card>
          </a-col>
          <a-col :xl="{span: 6}" :lg="{span: 12}" :sm="{span: 24}">
            <a-card :loading="loading" class="gauge-card" title="产品B/单耗">
              <gauge-chart class="gauge-chart" id="ECReportGauge2" :value="energySingle.B"></gauge-chart>
            </a-card>
          </a-col>
          <a-col :xl="{span: 6}" :lg="{span: 12}" :sm="{span: 24}">
            <a-card :loading="loading" class="gauge-card" title="产品C/单耗">
              <gauge-chart class="gauge-chart" id="ECReportGauge3" :value="energySingle.C"></gauge-chart>
            </a-card>
          </a-col>
          <a-col :xl="{span: 6}" :lg="{span: 12}" :sm="{span: 24}">
            <a-card :loading="loading" class="gauge-card" title="产品D/单耗">
              <gauge-chart class="gauge-chart" id="ECReportGauge4" :value="energySingle.D"></gauge-chart>
            </a-card>
          </a-col>
        </a-row>
      </div>
    </a-spin>
  </div>
</template>

<script>
import BarChart from '@comp/chart/BarChart'
import QuerySelect from '@comp/QuerySelect'
import {getAction, postAction} from '@/api/manage'
import GaugeChart from '@comp/chart/GaugeChart'

export default {
  name: 'EnergyConsumption',
  components: {GaugeChart, QuerySelect, BarChart},
  computed: {
    height() {
      return this.$store.getters.bodyHeight - 135
    }
  },
  data() {
    return {
      macDurationXAxis: [],
      macDurationSeries: [],
      loading: false,
      casForm: {
        companyId: '',
        factoryId: '',
        areaId: ''
      },
      macReportForm: {
        lineId: '',
        beginTime: '',
        endTime: ''
      },
      macReportFormRules: {
        beginTime: [
          {required: true, message: '必填校验'}
        ],
        endTime: [
          {required: true, message: '必填校验'}
        ],
        lineId: [
          {required: true, message: '必填校验'}
        ]
      },
      switchUnit: 'day',
      regionUnit: 'line',
      series: [
        {name: '产量', data: [13, 10, 3, 12, 15, 30, 7], yIndex: 0},
        {name: '能耗', data: [127, 142, 356, 89, 270, 221, 127], yIndex: 1}
      ],
      yAxis: [
        {
          name: '片数',
          axisLabel: {
            formatter: '{value}',
            color: '#999',
            textStyle: {
              fontSize: 18
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
            lineStyle: {
              color: 'rgba(131,101,101,0.2)',
              type: 'dashed'
            }
          }
        }, {
          name: 'KWh',
          axisLabel: {
            formatter: '{value}',
            color: '#999',
            textStyle: {
              fontSize: 18
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
      barChartLegend: ['line1', 'line2', 'line3', 'line4', 'line5'],
      energySingle: {A: 800, B: 458, C: 468, D: 785}
    }
  },
  methods: {
    async searchEvent() {
      this.loading = true
      let macResult = await getAction('/eam/machineInfo/getMachineListByLine', this.macReportForm)
      let macList = macResult['result']
      this.macReportForm['format'] = this.getFormat(this.switchUnit)
      postAction('/eam/machineInfo/selectMacStateDuration', this.macReportForm).then(res => {
        if (res['code'] === 200) {
          let macDurationList = res['result']
          if (macDurationList && macDurationList.length > 0) {
            let xAxis = []
            let series = []
            let obj = {}
            for (const md of macDurationList) {
              if (!obj[md['date']]) {
                xAxis.push(md['date'])
                obj[md['date']] = 1
              }
            }
            macList.forEach(mac => {
              let ser = {name: '', data: []}
              ser['name'] = mac['name']
              macDurationList.forEach(dr => {
                if (ser['name'] === dr['name']) {
                  ser['data'].push(dr['value'])
                }
              })
              series.push(ser)
            })
            this.macDurationSeries = series
            this.macDurationXAxis = xAxis
          } else {
            this.macDurationSeries = []
            this.macDurationXAxis = []
          }
          this.loading = false
        } else {
          this.$message.error(res['message'])
          this.loading = false
        }
      })
    },
    resetEvent() {
      this.resetFormModel()
      this.$refs.macReportCompany.clear()
      this.$refs.macReportFactory.clear()
      this.$refs.macReportArea.clear()
      this.$refs.macReportLine.clear()
    },
    handleCompanyChange(value) {
      this.$refs.macReportFactory.clear()
      this.$refs.macReportArea.clear()
      this.$refs.macReportLine.clear()
    },
    handleFactoryChange(value) {
      this.$refs.macReportArea.clear()
      this.$refs.macReportLine.clear()
    },
    handleAreaChange(value) {
      this.$refs.macReportLine.clear()
    },
    resetFormModel() {
      for (const key in this.macReportForm) {
        this.macReportForm[key] = ''
      }
    },
    handlerUnitChange(e) {
      let that = this
      this.switchUnit = e.target.value
      if (e.target.value === 'month') {
        this.series = [
          {name: '产量', data: [148, 130, 73, 212, 415, 320, 127], yIndex: 0},
          {name: '能耗', data: [1270, 1420, 3506, 890, 2070, 2021, 1207], yIndex: 1}
        ]
        this.energySingle = {A: 958, B: 756, C: 821, D: 998}
      } else {
        this.series = [
          {name: '产量', data: [13, 10, 3, 12, 15, 30, 7], yIndex: 0},
          {name: '能耗', data: [127, 142, 356, 89, 270, 221, 127], yIndex: 1}
        ]
        this.energySingle = {A: 800, B: 458, C: 468, D: 785}
      }
      this.$refs.xForm.validate(function (error) {
        if (error) {
          console.log('必填项未填')
        } else {
          that.searchEvent()
        }
      })
    },
    handlerRegionUnitChange(e) {
      let that = this
      this.regionUnit = e.target.value
      if (e.target.value === 'area') {
        this.barChartLegend = ['area1', 'area2', 'area3']
      } else {
        this.barChartLegend = ['line1', 'line2', 'line3', 'line4', 'line5']
      }
    },
    getFormat(unit) {
      switch (unit) {
        case 'day':
          return '%Y-%m-%d'
        case 'month':
          return '%Y-%m'
        case 'year':
          return '%Y'
        default:
          return '%Y-%m-%d'
      }
    }
  }
}
</script>

<style lang="less" scoped>
.main-mac-report-div {

}

.report-panel {
  width: 100%;
  margin-top: 8px;
  height: calc(100vh - 140px);
  border-radius: 5px;
  background: #f0f2f5;
}

.mac-opera-report-chart {
  margin-top: 8px;
  border-radius: 5px;
  background: #ffffff;
  width: 100%;
  height: calc(55% - 30px);
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