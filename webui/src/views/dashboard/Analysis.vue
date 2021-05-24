<template>
  <div class="page-header-index-wide">
    <a-row :gutter="10" class="card-area">
      <a-col :sm="24" :md="12" :xl="6">
        <chart-card :loading="loading" title="今日出货数量" :total="cardData.TotalCapacity.DaliyCapacity + ' 片'">
          <a-tooltip title="详细图表" slot="action">
            <a-icon type="area-chart" @click="handlerChangeCurrentTag('TotalCapacity')" />
          </a-tooltip>
          <div>
            <trend :flag="(cardData.TotalCapacity.DayDiff > 100)?'up':'down'">
              <span slot="term">日同比</span>
              {{ cardData.TotalCapacity.DayDiff }}%
            </trend>
          </div>
          <template slot="footer">月总出货数量<span> {{ cardData.TotalCapacity.MonthCapacity }} 片</span></template>
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
        <chart-card :loading="loading" title="当前在制产品数量" :total="cardData.WIPProd.Count.toString() + ' 片'">
          <a-tooltip title="转到详细报表" slot="action">
            <router-link to="/production/WIPSearchReport">
              <a-icon type="arrow-right" />
            </router-link>
          </a-tooltip>
          <div>
            <trend :flag="(cardData.WIPProd.DelayCount > 0)?'up':'down'">
              <span slot="term">疑似滞留</span>
              {{ cardData.WIPProd.DelayCount }} 片
            </trend>
          </div>
          <template slot="footer">WIP 数据</template>
        </chart-card>
      </a-col>
      <a-col :sm="24" :md="12" :xl="6">
        <chart-card :loading="loading" title="今日不良品数量" :total="cardData.PEMS_MCHW_COP.COP.toString()">
          <a-tooltip title="转到详细报表" slot="action">
            <router-link to="/production/DefectRecordsReport">
              <a-icon type="arrow-right" />
            </router-link>
          </a-tooltip>
          <div>
            <mini-bar :height="40" :data-source="cardData.PEMS_MCHW_COP.COPTrendDataSource" x="date" y="value" />
          </div>
          <template slot="footer">
            <div style="display: flex; justify-content: space-between;">
              <label style="width: 33%; overflow: hidden; text-overflow: ellipsis;">当月不良品数量</label><span> {{ cardData.PEMS_RCHW_PowerT.MonthTotalElc }} 个</span>
              <label style="width: 33%; overflow: hidden; text-overflow: ellipsis;">当月返工产品数量</label><span> {{ cardData.PEMS_RCHW_PowerT.MonthTotalElc }} 个</span>
            </div>
          </template>
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
import { getRangeOfTime } from '@/utils/util'
import moment from 'dayjs'
import { executeSQL } from '@api/api'

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
        TotalCapacity: {
          DaliyCapacity: 0,
          MonthCapacity: 0,
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
        WIPProd: {
          Count: 0,
          DelayCount: 0
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
      currentTag: 'TotalCapacity',
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
    // this.refreshDashboardLine()
  },
  methods: {
    initData() {
      this.refreshWIPData()
    },
    refreshWIPData() {
      let params = {
        sql_name: 'getWIPInfoByParams',
        lot: '',
        product: '',
        operation: '',
        spec: '',
        flow: ''
      }
      executeSQL(params).then((res) => {
        let product_infos = res['result']
        this.cardData.WIPProd.Count = product_infos.length
        this.cardData.WIPProd.DelayCount = (product_infos.filter((prod) => {return getRangeOfTime(prod['last_event_time']) > 30})).length
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

    },
    refreshDashboardPie() {

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