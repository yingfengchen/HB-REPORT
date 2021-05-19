<template>
  <div class="main-container">
    <a-row>
      <a-col :span="24" style="text-align: center; height: 6%">
        <dv-decoration2 v-if="isDesktop()" style="width:25%;height:100%;display: inline-block;"></dv-decoration2>
        <dv-decoration8 style="width:12%;height:50px;display: inline-block;"></dv-decoration8>
        <div class="title">物联智控中心</div>
        <dv-decoration8 :reverse="true" style="width:12%;height:50px;display: inline-block;"></dv-decoration8>
        <dv-decoration2 v-if="isDesktop()" style="width:12%;height:100%;display: inline-block;"></dv-decoration2>
        <dv-decoration3 v-if="isDesktop()" style="width:14%;height:100%;display: inline-block; vertical-align: top"></dv-decoration3>
      </a-col>
      <a-col :lg="{span: 6}" :sm="{span: 24}">
        <a-row :style="{ height: height + 'px' }">
          <a-col :span="24" style="height: 28%">
            <dv-border-box11 class="card-dv-box" title="设备类型台数">
              <pie-chart id="dashboardPieChart2" :legend-list='areaList' :datasource='pieData' class="card-container-chart"></pie-chart>
            </dv-border-box11>
          </a-col>
          <a-col :span="24" style="height: 28%">
            <dv-border-box11 class="card-dv-box" title="区域设备台数">
              <pie-chart id="dashboardPieChart1" :legend-list='areaList' :datasource='pieData' class="card-container-chart"></pie-chart>
            </dv-border-box11>
          </a-col>
          <a-col :span="24" style="height: 44%">
            <dv-border-box11 class="card-dv-box" title="区域平衡稼动率">
              <bar-chart id="dashboardBarChart" class="card-container-chart"></bar-chart>
            </dv-border-box11>
          </a-col>
        </a-row>
      </a-col>
      <a-col :lg="{span: 12}" :sm="{span: 24}">
        <a-row :style="{ height: height + 'px' }">
          <a-col :span="24"  style="height: 56%">
            <map-chart id="dashboardMap" style="width: 100%; height: 100%"></map-chart>
          </a-col>
          <a-col :span="24" style="height: 44%">
            <dv-border-box11 class="card-dv-box" title="设备稼动率">
              <horizontal-bar-chart id="dashboardHorizBarChart" class="card-container-chart"></horizontal-bar-chart>
            </dv-border-box11>
          </a-col>
        </a-row>
      </a-col>
      <a-col :lg="{span: 6}" :sm="{span: 24}">
        <a-row :style="{ height: height + 'px' }">
          <a-col :span="24" style="height: 28%">
            <dv-border-box11 class="card-dv-box" title="宕机设备">
              <dv-scroll-board
                style="width: calc(100% - 20px); height: 97%; margin-left: 20px"
                :config="datasource"
              />
            </dv-border-box11>
          </a-col>
          <a-col :span="24" style="height: 28%">
            <dv-border-box11 class="card-dv-box" title="各区域设备维保总次数">
              <no-area-line-chart id="dashboardNoAreaLineChart" class="card-container-chart"></no-area-line-chart>
            </dv-border-box11>
          </a-col>
          <a-col :span="24" style="height: 44%">
            <dv-border-box11 class="card-dv-box" title="设备维月度运行时长">
              <k-line-chart id="dashboardKLineChart" class="card-container-chart"></k-line-chart>
            </dv-border-box11>
          </a-col>
        </a-row>
      </a-col>
    </a-row>
  </div>
</template>

<script>
import MapChart from "@comp/chart/MapChart";
import HorizontalBarChart from "@comp/chart/HorizontalBarChart";
import BarChart from "@comp/chart/BarChart";
import PieChart from "@comp/chart/PieChart";
import NoAreaLineChart from "@comp/chart/NoAreaLineChart";
import KLineChart from "@comp/chart/KLineChart";
import { mixinDevice } from '@/utils/mixin.js'

export default {
  name: "BigScreen",
  components: {
    KLineChart,
    NoAreaLineChart,
    PieChart,
    BarChart,
    HorizontalBarChart,
    MapChart
  },
  mixins: [mixinDevice],
  computed: {
    height() {
      return this.$store.getters.bodyHeight - 64
    }
  },
  data() {
    return {
      datasource: {
        header: ['名称', '归属', '程度', '范围'],
        headerHeight: 40,
        data: [
          ['行1列1', '行1列2', '行1列3', '200km-300km'],
          ['行2列1', '行2列2', '行2列3', '< 500km'],
          ['行3列1', '行3列2', '行3列3', '20km-890km'],
          ['行4列1', '行4列2', '行4列3', '200km-300km'],
          ['行5列1', '行5列2', '行5列3', '200km-300km'],
          ['行6列1', '行6列2', '行6列3', '200km-300km'],
          ['行7列1', '行7列2', '行7列3', '200km-300km'],
          ['行8列1', '行8列2', '行8列3', '200km-300km'],
          ['行9列1', '行9列2', '行9列3', '200km-300km'],
          ['行10列1', '行10列2', '行10列3', '200km-300km']
        ],
        headerBGC: 'rgba(25,47,114,0.65)',
        oddRowBGC: 'rgba(255,255,255,0)',
        evenRowBGC: 'rgba(7,20,105,0.56)',
        index: true,
        columnWidth: [50],
        align: ['center']
      },
      areaList: [],
      pieData: [
        { value: 335, name: 'Line 1 车间' },
        { value: 310, name: 'Line 12 车间' },
        { value: 234, name: 'Line 2 车间' },
        { value: 135, name: 'Line 11 车间' },
        { value: 1548, name: 'Line 10 车间' }
      ],
    }
  }
}
</script>

<style lang="less" scoped>
.main-container {
  background: url("~@assets/dashboardbg.png") no-repeat;
  background-size: cover;
  overflow: hidden;

  /deep/ .header {
    height: 40px
  }

  /deep/ .rank{
    height: calc(100% - 15px);
    overflow-y: auto;
    padding: 10px 12px;
    .list {
      margin: 0;
      li{
        margin-top: 6px;
      }
    }
  }
}

.title {
  display: inline-block;
  color: #c4c4c4;
  line-height: 64px;
  font-size: 30px;
  vertical-align: top;
  margin: 0 13px 0 13px;
}

.card-header-title {
  height: 30px;
  padding: 5px 0 0 10px;
  color: #acd7ff;
  box-sizing: border-box;
  font-weight: bolder;
  font-family: "Chinese Quote", -apple-system, BlinkMacSystemFont, "Segoe UI", "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "Helvetica Neue", Helvetica, Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
  text-align: center;
  font-size: 16px;
}

.card-container-chart {
  width: 100%;
  height: 100%
}

.card-dv-box {
  width: 100%;
  height: 100%;
  padding-top: 48px;
  padding-right: 6px;
}
</style>