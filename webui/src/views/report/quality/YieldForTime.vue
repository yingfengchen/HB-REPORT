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
          <vxe-form-item span="5" title="日期" field="queryDate" title-overflow="ellipsis">
            <template #default>
              <vxe-input v-model="form.queryDate" placeholder="时间选择" type="date" :editable="false" />
            </template>
          </vxe-form-item>
          <vxe-form-item span="5" title="客户" field="customer">
            <query-select
              ref="xLine"
              v-model="form.customer"
              :options="customer"
              :option-config="{label: 'DESCRIPTION', value: 'NAME'}"
              :display-first-default="customer.displayFirst"
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
      </a-col>
    </a-row>
    <a-spin :spinning="loading" tip="查询中..." id="FPYReport">
      <a-row>
        <a-col :span="24">
          <a-card>
            <div :style="{height: '160px'}">
              <a-descriptions title="查询信息" bordered>
                <a-descriptions-item label="客户">
                  {{ displayInfo.customer }}
                </a-descriptions-item>
                <a-descriptions-item label="产品规格">
                  {{ displayInfo.inch }}
                </a-descriptions-item>
                <a-descriptions-item label="日期">
                  {{ displayInfo.date }}
                </a-descriptions-item>
                <a-descriptions-item label="周">
                  {{ displayInfo.week }}
                </a-descriptions-item>
                <a-descriptions-item label="本周范围">
                  {{ displayInfo.weekRange }}
                </a-descriptions-item>
              </a-descriptions>
            </div>
          </a-card>
        </a-col>
        <a-col :span="24">
          <a-card title="不良占比以及良率趋势">
            <div :style="{height: '500px'}">
              <line-chart
                class="cold-capacity-chart"
                id="trendYield"
                :show-split-line="true"
                :boundary-gap="true"
                :y-axis="yieldChart.yAxis"
                :x-axis="yieldChart.xAxis"
                :series-data="yieldChart.series"
              />
            </div>
          </a-card>
        </a-col>
      </a-row>
    </a-spin>
  </div>
</template>

<script>
import QuerySelect from '@comp/QuerySelect'
import LineChart from '@comp/chart/LineChart'
import { zeroFill } from '@/utils/util'
const { executeSQL } = require('@api/api')

export default {
  name: 'YieldForTime',
  components: { LineChart, QuerySelect },
  computed: {
    height() {
      return this.$store.getters.bodyHeight - 135
    }
  },
  data() {
    return {
      form: {
        queryDate: '',
        customer: '',
        inch: ''
      },
      formRules: {
        queryDate: [
          {required: true}
        ],
        customer: [
          {required: true}
        ],
      },
      customer: {
        options: [],
        displayFirst: false
      },
      displayInfo: {
        customer: '',
        inch: '',
        date: '',
        week: '',
        weekRange: ''
      },
      yieldChart: {
        xAxis: [],
        legend: [],
        series: [],
        yAxis: [
        {
          name: '%',
          axisLabel: {
            formatter: '{value} %',
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
      ]
      },
      loading: false
    }
  },
  mounted() {
    this.initData()
  },
  methods: {
    initData() {
      let _this = this
      executeSQL({ sql_name: 'getCustomerList' }).then(res => {
        if (res && res['code'] === 200) {
          _this.customer.options = res['result']
        } else {
          _this.$message.error(res['message'])
        }
      })

      executeSQL({ sql_name: 'getAllDefectType' }).then(res => {
        if (res && res['code'] === 200) {
          _this.yieldChart.legend = res['result']
        } else {
          _this.$message.error(res['message'])
        }
      })
    },
    handlerSubmit() {
      let _this = this
      this.yieldChart.xAxis = []
      this.yieldChart.series = []

      const year = this.form.queryDate.substr(0, 4)
      const quarter = this.getQuarterByDate(this.form.queryDate)
      const month = this.getMonthByDate(this.form.queryDate)
      const weekDay = this.getWeekDayByDate(this.form.queryDate)
      const week = this.getWeekByDate(this.form.queryDate)

      this.yieldChart.xAxis = [year, '年', ...(quarter.split(',')), '季度', ...(month.split(',')), '月', ...(week.split(',')), '周', ...(weekDay.split(','))]

      this.displayInfo.date = this.form.queryDate
      this.displayInfo.week = week.split(',')[week.split(',').length - 1]
      this.displayInfo.weekRange = weekDay.split(',')[0] + ' ~ ' + weekDay.split(',')[weekDay.split(',').length - 1]
      this.displayInfo.customer = this.form.customer
      this.displayInfo.inch = this.form.inch

      executeSQL({ sql_name: 'getYQMWDData', customer: this.form.customer, year: year, quarter: quarter, month: month, week: week, day: weekDay }).then(res => {
        if (res && res['code'] === 200) {
          const result = res['result']
          let total = {}
          let totalDefect = {}
          _this.yieldChart.xAxis.forEach(x => {
            total[x] = 0
            totalDefect[x] = 0
            for (let resultElement of result) {
              if(resultElement['TYPE'] === x){
                total[x] += resultElement['NG_COUNT']
                if(resultElement['DEFECT_TYPE'] !== 'OK'){
                  totalDefect[x] += resultElement['NG_COUNT']
                }
              }
            }
          })

          // 不良代码处理
          _this.yieldChart.legend.forEach(l => {
            let sery = {name: l['DEFECT_TYPE'], type: 'bar', stack: 'total', data: []}
            _this.yieldChart.xAxis.forEach(x => {
              let count = 0
              if(x !== '年' && x !== '季度' && x !== '月' && x !== '周') {
                for (let resultElement of result) {
                  if (resultElement['DEFECT_TYPE'] === l['DEFECT_TYPE'] && resultElement['TYPE'] === x) {
                    count = (resultElement['NG_COUNT'] ? resultElement['NG_COUNT'] / totalDefect[x] : 0).toFixed(1) * 100
                  }
                }
              }else{
                count = '-'
              }
              sery.data.push(count)
            })
            _this.yieldChart.series.push(sery)
          })

          // 良率处理
          let sery = {name: '良率', type: 'line', data: [], markLine: {data: 98}}
          _this.yieldChart.xAxis.forEach(x => {
            let count = 0
            if(x !== '年' && x !== '季度' && x !== '月' && x !== '周') {
              for (let resultElement of result) {
                if (resultElement['DEFECT_TYPE'] === 'OK' && resultElement['TYPE'] === x) {
                  count = (resultElement['NG_COUNT'] ? resultElement['NG_COUNT'] / total[x] : 0).toFixed(1) * 100
                }
              }
            }else {
              count = '-'
            }
            sery.data.push(count)
          })
          _this.yieldChart.series.push(sery)
        } else {
          console.log(res)
        }
      })
    },
    getQuarterByDate(date) {
      const d = new Date(date)
      const month = d.getMonth()
      if (month < 3) {
        return 'Q' + 1
      } else if (month < 6) {
        return 'Q' + 1 + ',Q' + 2
      } else if (month < 9) {
        return 'Q' + 2 + ',Q' + 3
      }
      return 'Q' + 3 + ',Q' + 4
    },
    getMonthByDate(date) {
      const d = new Date(date)
      const month = d.getMonth()+1
      let result = ''
      if (month-2 > 0) {
        result += d.getFullYear() + '-' + zeroFill(month-2) + ','
      }
      if (month-1 > 0) {
        result += d.getFullYear() + '-' + zeroFill(month-1) + ','
      }
      return result + d.getFullYear() + '-' + zeroFill(month)
    },
    getWeekByDate(time){
      let firstTime = new Date(time.substring(0, 4) + '-01-01');
      let timestamp = new Date(time).getTime();
      let dayLong = 24*60*60*1000;
      const offset_day = firstTime.getDay() > 0 ? (8 - firstTime.getDay()) : 1
      const offset = (timestamp - firstTime.getTime() - offset_day * dayLong)/dayLong
      const week = Math.floor((offset / 7) + 1)
      let result = ''
      if (week-3 > 0) {
        result += 'W' + (week-3) + ','
      }
      if (week-2 > 0) {
        result += 'W' + (week-2) + ','
      }
      if (week-1 > 0) {
        result += 'W' + (week-1) + ','
      }
      return result + 'W' + week
    },
    getWeekDayByDate(time){
      let dayCode = new Date(time).getDay();
      let dayLong = 24*60*60*1000;
      let result = ''
      const startDate = new Date(time).getTime() - ((dayCode===0?7:dayCode) * dayLong)
      for (let i = 1; i < 8; i++) {
        const timeStamp = startDate + i * dayLong
        const date_i = new Date(timeStamp)
        result += date_i.getFullYear() + '-' + zeroFill(date_i.getMonth() + 1) + '-' + zeroFill(date_i.getDate())
        if(i<7){
          result += ','
        }
      }
      return result
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