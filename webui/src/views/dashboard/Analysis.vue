<template>
  <div class="page-header-index-wide">
    <a-row :gutter="10" class="card-area">
      <a-col :sm="24" :md="12" :xl="6">
        <chart-card :loading="loading" title="今日产能" :total="cardData.PEMS_CH_PowerT.DayTotalElc + ' 片'">
          <a-tooltip title="详细图表" slot="action">
            <a-icon type="area-chart" @click="handlerChangeCurrentTag('PEMS_CH_PowerT')" />
          </a-tooltip>
          <div>
            <trend :flag="(cardData.PEMS_CH_PowerT.DayDiff > 100)?'up':'down'">
              <span slot="term">日同比</span>
              {{ cardData.PEMS_CH_PowerT.DayDiff }}%
            </trend>
          </div>
          <template slot="footer">月总产能<span> {{ cardData.PEMS_CH_PowerT.MonthTotalElc }} 片</span></template>
        </chart-card>
      </a-col>
      <a-col :sm="24" :md="12" :xl="6">
        <chart-card :loading="loading" title="今日出货数量" :total="cardData.PEMS_LCHW_PowerT.DayTotalElc + ' 片'">
          <a-tooltip title="详细图表" slot="action">
            <a-icon type="area-chart" @click="handlerChangeCurrentTag('PEMS_LCHW_PowerT')" />
          </a-tooltip>
          <div>
            <mini-area :data-source="cardData.PEMS_LCHW_PowerT.TrendDataSource" x="date" y="value" />
          </div>
          <template slot="footer">月出货数量<span> {{ cardData.PEMS_LCHW_PowerT.MonthTotalElc }} 片</span>
          </template>
        </chart-card>
      </a-col>
      <a-col :sm="24" :md="12" :xl="6">
        <chart-card :loading="loading" title="今日良率" :total="cardData.PEMS_MCHW_PowerT.DayTotalElc + ' %'">
          <a-tooltip title="详细图表" slot="action">
            <a-icon type="area-chart" @click="handlerChangeCurrentTag('PEMS_MCHW_PowerT')" />
          </a-tooltip>
          <div>
            <mini-area :data-source="cardData.PEMS_MCHW_PowerT.TrendDataSource" x="date" y="value" />
          </div>
          <template slot="footer">月良率<span>  {{ cardData.PEMS_MCHW_PowerT.MonthTotalElc }} %</span>
          </template>
        </chart-card>
      </a-col>
      <a-col :sm="24" :md="12" :xl="6">
        <chart-card :loading="loading" title="今日 Alarm 数量" :total="cardData.PEMS_RCHW_PowerT.DayTotalElc + ' 个'">
          <a-tooltip title="详细图表" slot="action">
            <a-icon type="area-chart" @click="handlerChangeCurrentTag('PEMS_RCHW_PowerT')" />
          </a-tooltip>
          <div>
            <mini-area :data-source="cardData.PEMS_RCHW_PowerT.TrendDataSource" x="date" y="value" />
          </div>
          <template slot="footer">月 Alarm 数量<span> {{ cardData.PEMS_RCHW_PowerT.MonthTotalElc }} 个</span>
          </template>
        </chart-card>
      </a-col>
      <a-col :sm="24" :md="12" :xl="6">
        <chart-card :loading="loading" title="低温系统COP" :total="cardData.PEMS_LCHW_COP.COP.toString()">
          <a-tooltip title="详细图表" slot="action">
            <a-icon type="area-chart" @click="handlerChangeCurrentTag('PEMS_LCHW_COP', false)" />
          </a-tooltip>
          <div>
            <mini-bar :height="40" :data-source="cardData.PEMS_LCHW_COP.COPTrendDataSource" x="date" y="value" />
          </div>
          <template slot="footer">低温系统COP</template>
        </chart-card>
      </a-col>
      <a-col :sm="24" :md="12" :xl="6">
        <chart-card :loading="loading" title="中温系统COP" :total="cardData.PEMS_MCHW_COP.COP.toString()">
          <a-tooltip title="详细图表" slot="action">
            <a-icon type="area-chart" @click="handlerChangeCurrentTag('PEMS_MCHW_COP', false)" />
          </a-tooltip>
          <div>
            <mini-bar :height="40" :data-source="cardData.PEMS_MCHW_COP.COPTrendDataSource" x="date" y="value" />
          </div>
          <template slot="footer">中温系统COP</template>
        </chart-card>
      </a-col>
      <a-col :sm="24" :md="12" :xl="6">
        <chart-card :loading="loading" title="热水系统COP" :total="cardData.PEMS_RCHW_COP.COP.toString()">
          <a-tooltip title="详细图表" slot="action">
            <a-icon type="area-chart" @click="handlerChangeCurrentTag('PEMS_RCHW_COP', false)" />
          </a-tooltip>
          <div>
            <mini-bar :height="40" :data-source="cardData.PEMS_RCHW_COP.COPTrendDataSource" x="date" y="value" />
          </div>
          <template slot="footer">热水系统COP</template>
        </chart-card>
      </a-col>
    </a-row>

    <a-row :gutter="5">
      <a-col :xl="16" :lg="16" :md="16" :sm="24" :xs="24">
        <a-card :loading="loading" :bordered="false" :body-style="{padding: '0'}">
          <div class="salesCard">
            <a-tabs default-active-key="1" :tab-bar-style="{paddingLeft: '16px'}">
              <div class="extra-wrapper" slot="tabBarExtraContent">
                <div class="extra-item">
                  <a-radio-group :value="switchUnit" @change="handlerUnitChange">
                    <a-radio-button value="%Y-%m-%d %H">时</a-radio-button>
                    <a-radio-button value="%Y-%m-%d">日</a-radio-button>
                    <a-radio-button value="%Y-%m">月</a-radio-button>
                  </a-radio-group>
                </div>
                <a-range-picker
                  @change="handlerTimeChange"
                  :style="{width: '256px'}"
                  :default-value="[startTime, endTime]"
                />
              </div>
              <a-tab-pane loading="true" tab="耗电趋势" key="1">
                <a-row>
                  <a-col :xl="24" :lg="24" :md="24" :sm="24" :xs="24">
                    <line-chart
                      id="dashboardLineChart"
                      :style="{height: (height-556) + 'px'}"
                      :x-axis="lineChartData.legends"
                      :series-data="lineChartData.series"
                    />
                  </a-col>
                </a-row>
              </a-tab-pane>
            </a-tabs>
          </div>
        </a-card>
      </a-col>
      <a-col :xl="8" :lg="8" :md="8" :sm="24" :xs="24">
        <a-card :loading="loading" title="各系统电耗占比" :bordered="false" :body-style="{padding: '0'}">
          <div class="extra-wrapper" slot="extra">
            <div class="extra-item">
              <a-radio-group :value="pieChartSwitchUnit" @change="handlerPieChartSwitchUnitChange">
                <a-radio-button value="day_value">今日</a-radio-button>
                <a-radio-button value="month_value">当月</a-radio-button>
                <a-radio-button value="year_value">当年</a-radio-button>
              </a-radio-group>
            </div>
          </div>
          <a-row>
            <a-col :xl="24" :lg="24" :md="24" :sm="24" :xs="24">
              <pie-chart :style="{height: (height-540) + 'px'}" :datasource="pieChartData.datasource"
                         :legend-list="pieChartData.legends"
                         :total-value="pieChartData.totalValue" />
            </a-col>
          </a-row>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script>
import ChartCard from '@/components/ChartCard'
import MiniBar from '@/components/chart/MiniBar'
import MiniArea from '@comp/chart/MiniArea'
import Trend from '@comp/Trend'
import LineChart from '@comp/chart/LineChart'
import PieChart from '@comp/chart/PieChart'
import {
  getCumulative,
  getDayMonthDiffValueOfTags,
  getInstantaneous,
  getTrendOfOneTag24Hour,
  getTwoDayDiffRateOfOnTag
} from '@api/energyApi'
import { transferStringToArray } from '@/utils/util'
import moment from 'dayjs'

export default {
  name: 'IndexEnergy',
  components: {
    PieChart,
    LineChart,
    ChartCard,
    MiniBar,
    MiniArea,
    Trend
  },
  computed: {
    height() {
      return this.$store.getters.bodyHeight
    }
  },
  data() {
    return {
      loading: true,
      cardData: {
        PEMS_CH_PowerT: {
          DayTotalElc: 0,
          MonthTotalElc: 0,
          DayDiff: 0
        },
        PEMS_LCHW_PowerT: {
          DayTotalElc: 0,
          MonthTotalElc: 0,
          TrendDataSource: [],
          COP: 0,
          COPTrendDataSource: []
        },
        PEMS_MCHW_PowerT: {
          DayTotalElc: 0,
          MonthTotalElc: 0,
          TrendDataSource: [],
          COP: 0,
          COPTrendDataSource: []
        },
        PEMS_RCHW_PowerT: {
          DayTotalElc: 0,
          MonthTotalElc: 0,
          TrendDataSource: [],
          COP: 0,
          COPTrendDataSource: []
        },
        PEMS_LCHW_COP: {
          COP: 0,
          COPTrendDataSource: []
        },
        PEMS_MCHW_COP: {
          COP: 0,
          COPTrendDataSource: []
        },
        PEMS_RCHW_COP: {
          COP: 0,
          COPTrendDataSource: []
        }
      },
      switchUnit: '%Y-%m-%d',
      pieChartSwitchUnit: 'day_value',
      currentTag: 'PEMS_CH_PowerT',
      startTime: moment(new Date(new Date().getTime() - 1000 * 60 * 60 * 24 * 7)).format('YYYY-MM-DD'),
      endTime: moment(new Date()).format('YYYY-MM-DD'),
      lineChartData: {
        legends: [],
        series: []
      },
      pieChartData: {
        datasource: [],
        legends: [],
        totalValue: 0
      },
      diffDataList: [],
      currentDiffFlag: true
    }
  },
  created() {
    setTimeout(() => {
      this.loading = !this.loading
    }, 600)
    this.initData()
  },
  mounted() {
    this.refreshDashboardLine()
  },
  methods: {
    initData() {
      this.getDiffValue()
      this.getInstantaneousValue()
      this.get24HourTrend()
      this.getDiffRate()
    },
    getDiffValue() {
      let params = {
        tagList: '' +
          'PEMS_LCHW_PowerT.VAL_Actl,' +
          'PEMS_MCHW_PowerT.VAL_Actl,' +
          'PEMS_RCHW_PowerT.VAL_Actl,' +
          'PEMS_CH_PowerT.VAL_Actl'
      }
      getDayMonthDiffValueOfTags(params).then(res => {
        this.diffDataList = res
        res.forEach(r => {
          this.cardData[r['tag_name']]['DayTotalElc'] = r['day_value']
          this.cardData[r['tag_name']]['MonthTotalElc'] = r['month_value']
        })
        this.refreshDashboardPie()
      })
    },
    getInstantaneousValue() {
      let params = {
        tagList: '' +
          'PEMS_LCHW_COP.VAL_Actl,' +
          'PEMS_MCHW_COP.VAL_Actl,' +
          'PEMS_RCHW_COP.VAL_Actl'
      }
      getInstantaneous(params).then(res => {
        res.forEach(r => {
          this.cardData[r['tag_name']]['COP'] = r['value']
        })
      })
    },
    get24HourTrend() {
      let params = { diff_flag: true, tag: 'PEMS_LCHW_PowerT.VAL_Actl' }
      getTrendOfOneTag24Hour(params).then(res => {
        this.cardData.PEMS_LCHW_PowerT.TrendDataSource = res
      })

      params = { diff_flag: true, tag: 'PEMS_MCHW_PowerT.VAL_Actl' }
      getTrendOfOneTag24Hour(params).then(res => {
        this.cardData.PEMS_MCHW_PowerT.TrendDataSource = res
      })

      params = { diff_flag: true, tag: 'PEMS_RCHW_PowerT.VAL_Actl' }
      getTrendOfOneTag24Hour(params).then(res => {
        this.cardData.PEMS_RCHW_PowerT.TrendDataSource = res
      })

      params = { diff_flag: false, tag: 'PEMS_LCHW_COP.VAL_Actl' }
      getTrendOfOneTag24Hour(params).then(res => {
        this.cardData.PEMS_LCHW_COP.COPTrendDataSource = res
      })

      params = { diff_flag: false, tag: 'PEMS_MCHW_COP.VAL_Actl' }
      getTrendOfOneTag24Hour(params).then(res => {
        this.cardData.PEMS_MCHW_COP.COPTrendDataSource = res
      })

      params = { diff_flag: false, tag: 'PEMS_RCHW_COP.VAL_Actl' }
      getTrendOfOneTag24Hour(params).then(res => {
        this.cardData.PEMS_RCHW_COP.COPTrendDataSource = res
      })
    },
    getDiffRate() {
      let params = { tag: 'PEMS_CH_PowerT.VAL_Actl' }
      getTwoDayDiffRateOfOnTag(params).then(res => {
        if (res.length > 0) {
          this.cardData.PEMS_CH_PowerT.DayDiff = (res[0]['y'] / res[1]['y'] * 100).toFixed(2)
        }
      })
    },
    handlerUnitChange(e) {
      let that = this
      that.switchUnit = e.target.value
      this.refreshDashboardLine()
    },
    handlerPieChartSwitchUnitChange(e) {
      let that = this
      that.pieChartSwitchUnit = e.target.value
      this.refreshDashboardPie()
    },
    handlerChangeCurrentTag(tag, diffFlag = true) {
      this.currentTag = tag
      this.currentDiffFlag = diffFlag
      this.refreshDashboardLine()
    },
    handlerTimeChange(date, dateString) {
      this.startTime = dateString[0]
      this.endTime = dateString[1]
      this.refreshDashboardLine()
    },
    refreshDashboardLine() {
      const params = {
        startTime: this.startTime,
        endTime: this.endTime,
        timeUnit: this.switchUnit,
        tagList: this.currentTag + '.VAL_Actl',
        diff_flag: this.currentDiffFlag
      }
      const seriesData = []

      getCumulative(params).then(res => {
        if (res.length > 0) {
          this.lineChartData.legends = transferStringToArray(res[0]['unit'])
          res.forEach(r => {
            let ser = { name: r['tag_name'], data: transferStringToArray(r['value']) }
            seriesData.push(ser)
          })
          this.lineChartData.series = seriesData
        }
      })
    },
    refreshDashboardPie() {
      let tagListScoped = [
        'PEMS_LCHW_PowerT',
        'PEMS_MCHW_PowerT',
        'PEMS_RCHW_PowerT'
      ]
      let datasourcePie = []
      let totalValue = 0

      this.pieChartData.legends = tagListScoped
      if (this.diffDataList.length > 0) {
        let temp = this.diffDataList.filter(d => tagListScoped.indexOf(d['tag_name']) > -1)
        temp.forEach(t => {
          datasourcePie.push({ name: t['tag_name'], value: t[this.pieChartSwitchUnit] })
          totalValue += t[this.pieChartSwitchUnit]
        })
      }
      this.pieChartData.datasource = datasourcePie
      this.pieChartData.totalValue = Number(totalValue.toFixed(1))
    }
  }
}
</script>

<style lang="less" scoped>
.page-header-index-wide {
  /deep/ .ant-card-head {
    padding: 0 0 0 24px;
  }

  /deep/ .ant-card-head-title {
    padding: 0;
  }

  /deep/ .ant-card-extra {
    padding: 0;
  }
}

.circle-cust {
  position: relative;
  top: 28px;
  left: -100%;
}

.extra-wrapper {
  line-height: 42px;
  padding-right: 24px;

  .extra-item {
    display: inline-block;
    margin-right: 5px;

    a {
      margin-left: 24px;
    }
  }
}

/* 首页访问量统计 */
.head-info {
  position: relative;
  text-align: left;
  padding: 0 32px 0 0;
  min-width: 125px;

  &.center {
    text-align: center;
    padding: 0 32px;
  }

  span {
    color: rgba(0, 0, 0, .45);
    display: inline-block;
    font-size: .95rem;
    line-height: 42px;
    margin-bottom: 4px;
  }

  p {
    line-height: 42px;
    margin: 0;

    a {
      font-weight: 600;
      font-size: 1rem;
    }
  }
}

.card-area {
  /deep/ .ant-col {
    margin-bottom: 14px;
  }
}
</style>