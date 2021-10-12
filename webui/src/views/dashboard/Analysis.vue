<template>
  <div class="page-header-index-wide">
    <a-row :gutter="5" style="margin-bottom: 10px">
      <a-col :lg="18">
        <a-card :loading="loading" :bordered="false" :body-style="{padding: '0', background: 'rgba(235,248,255,0.83)'}">
          <div style="height: 30px; margin: 5px; width: 100%; display: flex; ">
            <a-radio-group :value="layoutName" @change="handleLayoutChange">
              <a-radio-button value="HB_01">
                HB_01
              </a-radio-button>
              <a-radio-button value="HB_02">
                HB_02
              </a-radio-button>
            </a-radio-group>
            <div style="display: flex; justify-content: center; align-items: center; margin-left: 10px;">
              <div style="background: #a1ff9a; border-radius: 5px; height: 15px; width: 20px; margin-right: 5px" />
              <label style="font-weight: bold">RUN</label>
            </div>
            <div style="display: flex; justify-content: center; align-items: center; margin-left: 10px;">
              <div style="background: #fff86c; border-radius: 5px; height: 15px; width: 20px; margin-right: 5px" />
              <label style="font-weight: bold">IDLE</label>
            </div>
            <div style="display: flex; justify-content: center; align-items: center; margin-left: 10px;">
              <div style="background: #6ca8ff; border-radius: 5px; height: 15px; width: 20px; margin-right: 5px" />
              <label style="font-weight: bold">PM</label>
            </div>
            <div style="display: flex; justify-content: center; align-items: center; margin-left: 10px;">
              <div style="background: #ff6565; border-radius: 5px; height: 15px; width: 20px; margin-right: 5px" />
              <label style="font-weight: bold">TROUBLE</label>
            </div>
          </div>
          <div style="height: 480px">
            <svg-layout :layout-name="layoutName" />
          </div>
        </a-card>
      </a-col>
      <a-col :lg="6">
        <a-card style="height: 334px; margin-bottom: 10px" title="设备状态">
          <a-table  :columns="columns" :data-source="mac_data">

          </a-table>
        </a-card>
        <chart-card :loading="loading" title="今日 Alarm 数量" :total="cardData.PEMS_RCHW_PowerT.DayTotalElc + ' 个'">
          <div>
            <mini-bar :height="40" :data-source="cardData.PEMS_RCHW_PowerT.TrendDataSource" x="date" y="value" />
          </div>
          <template slot="footer">月 Alarm 数量<span> {{ cardData.PEMS_RCHW_PowerT.MonthTotalElc }} 个</span>
          </template>
        </chart-card>
      </a-col>
    </a-row>
    <a-row :gutter="10" class="card-area">
      <a-col :sm="24" :md="12" :xl="6">
        <chart-card :loading="loading" title="今日出货数量" :total="cardData.TotalCapacity.DaliyCapacity + ' 片'">
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
        <chart-card :loading="loading" title="今日良率" :total="cardData.Yield.TodayValue + ' %'">
          <div>
            <mini-area :data-source="cardData.Yield.TrendDataSource" x="date" y="RATE" />
          </div>
          <template slot="footer">月良率<span>  {{ cardData.Yield.MonthValue }} %</span>
          </template>
        </chart-card>
      </a-col>
      <a-col :sm="24" :md="12" :xl="6">
        <a-spin tip="数据加载中..." :spinning="spin.wip">
          <chart-card :loading="loading" title="当前在制产品数量" :total="cardData.WIPProd.Count + ' 片'">
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
        </a-spin>
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
              <label
                style="width: 33%; overflow: hidden; text-overflow: ellipsis;">当月不良品数量</label><span> {{ cardData.PEMS_RCHW_PowerT.MonthTotalElc }} 个</span>
              <label
                style="width: 33%; overflow: hidden; text-overflow: ellipsis;">当月返工产品数量</label><span> {{ cardData.PEMS_RCHW_PowerT.MonthTotalElc }} 个</span>
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
                    <a-radio-button value="YYYY-MM-DD HH24">时</a-radio-button>
                    <a-radio-button value="YYYY-MM-DD">日</a-radio-button>
                    <a-radio-button value="YYYY-MM">月</a-radio-button>
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
              <pie-chart :style="{height: (height-540) + 'px'}"
              />
            </a-col>
          </a-row>
        </a-card>
      </a-col>
    </a-row>
    <form-modal :visible="formModalVisible" :title="formModalTitle" :form-json="formModalJson"
                @close="handlerFormModalClose" />
  </div>
</template>

<script>
import ChartCard from '@/components/ChartCard'
import MiniBar from '@/components/chart/MiniBar'
import MiniArea from '@comp/chart/MiniArea'
import Trend from '@comp/Trend'
import LineChart from '@comp/chart/LineChart'
import PieChart from '@comp/chart/PieChart'
import { getCurrentTime, getRangeOfTime } from '@/utils/util'
import moment from 'dayjs'
import { executeSQL } from '@api/api'
import FormModal from '@views/dashboard/modal/FormModal'
import SvgLayout from '@views/dashboard/components/SvgLayout'

export default {
  name: 'IndexEnergy',
  components: {
    SvgLayout,
    FormModal,
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
        Yield: {
          TodayValue: 0,
          MonthValue: 0,
          TrendDataSource: []
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
      switchUnit: 'YYYY-MM-DD',
      pieChartSwitchUnit: 'day_value',
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
      currentDiffFlag: true,
      formModalVisible: false,
      formModalTitle: '',
      formModalJson: [],
      spin: {
        wip: true
      },
      layoutName: 'HB_01',
      columns: [],
      mac_data: []
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
      this.refreshRateData()
    },
    refreshWIPData() {
      let params = {
        sql_name: 'getWIPAsisCount'
      }
      executeSQL(params).then((res) => {
        if(res && res['success']) {
          let product_infos = res['result'][0]
          this.cardData.WIPProd.Count = parseInt(product_infos['total_count'])
          this.cardData.WIPProd.DelayCount = parseInt(product_infos['delay_count'])
          this.spin.wip = false
        }else{
          this.$notification['error']({
            message: '获取数据失败',
            description: res['message'],
          });
        }
      })
    },
    refreshRateData() {
      executeSQL({sql_name: 'get15DayRateList'}).then((res) => {
        if(res && res['success']) {
          const result = res['result']
          this.cardData.Yield.TrendDataSource = result
          this.cardData.Yield.TodayValue = result[0]['y']
        }else{
          this.$notification['error']({
            message: '获取数据失败',
            description: res['message'],
          });
        }
      })

      executeSQL({sql_name: 'getMonthRate'}).then((res) => {
        if(res && res['success']) {
          const result = res['result']
          this.cardData.Yield.MonthValue = result[0]['y']
        }else{
          this.$notification['error']({
            message: '获取数据失败',
            description: res['message'],
          });
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
    handlerTimeChange(date, dateString) {
      this.startTime = dateString[0]
      this.endTime = dateString[1]
      this.refreshDashboardLine()
    },
    refreshDashboardLine() {

    },
    refreshDashboardPie() {

    },
    handlerFormModalClose() {
      this.formModalVisible = false
      this.formModalJson = []
    },
    changeUL(str) {
      return this.changeUpperOrLower(str)
    },
    handleLayoutChange(e) {
      this.layoutName = e.target.value;
    }
  }
}
</script>

<style lang="less" scoped>
.page-header-index-wide {
  /deep/ .ant-card-head {
    padding: 0 0 0 24px;
  }

  /deep/ .ant-card-head-wrapper {
    height: 48px;

    .ant-card-head-title {
      padding: 0;
    }
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

.link-row {
  margin-bottom: 10px;

  .link-card {
    box-sizing: border-box;
    background: #ffffff;
    height: 110px;
    transition: all .3s;
    border: 1px solid #dddddd;
    border-radius: 4px;
    display: flex;
    align-items: center;

    .animation-reserve {
      -webkit-animation: flip-vertical-left 0.4s cubic-bezier(0.455, 0.030, 0.515, 0.955) both;
      animation: flip-vertical-left 0.4s cubic-bezier(0.455, 0.030, 0.515, 0.955) both;
    }

    label {
      margin-left: 10px;
    }

    i {
      width: 40px;
      height: 40px;
      font-size: 35px;
      font-weight: bolder;
      margin-left: 20px;
      text-align: center;
      line-height: 40px;
    }

    /deep/ svg {
      width: 40px;
      height: 40px;
      margin-left: 30px;
    }
  }

  .link-card:hover {
    box-shadow: 0 2px 8px #00000055;
    .animation-reserve {
      -webkit-animation: flip-vertical-right 0.4s cubic-bezier(0.455, 0.030, 0.515, 0.955) both;
      animation: flip-vertical-right 0.4s cubic-bezier(0.455, 0.030, 0.515, 0.955) both;
    }
  }
}

.layout-display {
  ::v-deep .ant-tabs-bar {
    margin: 0;
  }
}
</style>