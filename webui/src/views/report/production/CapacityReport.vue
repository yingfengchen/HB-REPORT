<template>
  <div class='trend-of-water-use-div'>
    <a-row class='params-panel'>
      <a-col :span='24'>
        <vxe-form
          ref='xWaterUseForm'
          title-width='100px'
          :loading='loading'
          :data='form'
          :rules='formRules'
          @submit='handlerSubmit'
        >
          <vxe-form-item span='5' title='开始时间' field='startTime'>
            <vxe-input v-model='form.startTime' placeholder='日期选择' type='date'></vxe-input>
          </vxe-form-item>
          <vxe-form-item span='5' title='结束时间' field='endTime'>
            <vxe-input v-model='form.endTime' placeholder='日期选择' type='date'></vxe-input>
          </vxe-form-item>
          <vxe-form-item span='5' title='站点' field='operation'>
            <query-select
              v-model="form.operation"
              url="/common/executeSql"
              method="post"
              :params="{sql_name: 'getAllOperations'}"
              :option-config="{label: 'description', value: 'name'}"
            />
          </vxe-form-item>
          <vxe-form-item span='5' title='产品规格' field='spec'>
            <query-select
              v-model="form.spec"
              url="/common/executeSql"
              method="post"
              :params="{sql_name: 'getAllProductSpec'}"
              :option-config="{label: 'name', value: 'value'}"
            />
          </vxe-form-item>
          <vxe-form-item>
            <template #default>
              <vxe-button type='submit' status='primary'>查询</vxe-button>
              <vxe-button type='reset'>重置</vxe-button>
            </template>
          </vxe-form-item>
        </vxe-form>
      </a-col>
    </a-row>
    <a-spin :spinning='loading' tip='查询中...'>
      <a-card title='设备产能趋势'>
        <div :style="{height: (height * 0.5)+'px'}">
          <a-row>
            <a-col :span='12'>
              <a-radio-group :value='switchWaterUseUnit' @change='handlerWaterUseUnitChange'>
                <a-radio-button value='%Y-%m-%d %H'>时</a-radio-button>
                <a-radio-button value='%Y-%m-%d'>日</a-radio-button>
                <a-radio-button value='%Y-%m'>月</a-radio-button>
              </a-radio-group>
            </a-col>
            <a-col :span='12' style='text-align: right'>
              <a-radio-group :value='switchArea' @change='handlerAreaChange'>
                <a-radio-button value='T'>总量</a-radio-button>
                <a-radio-button value='1'>Line 1</a-radio-button>
                <a-radio-button value='2'>Line 2</a-radio-button>
              </a-radio-group>
            </a-col>
          </a-row>
          <line-chart class='cold-capacity-chart' id='trendWaterUse' :show-split-line='true'
                      :x-axis='waterUseChartLegend'
                      :series-data='waterUseChartSeries' :y-axis='yAxis' />
        </div>
      </a-card>
      <a-card title='AB 班组产能对比' style='width: calc(50% - 4px); display: inline-block; margin-right: 8px'>
        <div :style="{height: (height * 0.5)+'px'}">
          <a-row>
            <a-col :span='12'>
              <a-radio-group :value='switchCOPUnit' @change='handlerCOPUnitChange'>
                <a-radio-button value='%Y-%m-%d %H'>时</a-radio-button>
                <a-radio-button value='%Y-%m-%d'>日</a-radio-button>
                <a-radio-button value='%Y-%m'>月</a-radio-button>
              </a-radio-group>
            </a-col>
          </a-row>
          <line-chart class='cop-chart' id='COPReport' :show-split-line='true' :x-axis='COPChartLegend'
                      :series-data='COPChartSeries' :y-axis='yAxis' />
        </div>
      </a-card>
      <a-card title='操作员产能趋势' style='width: calc(50% - 4px); display: inline-block'>
        <div :style="{height: (height * 0.5)+'px'}">
          <a-row>
            <a-col :span='12'>
              <label>所属站点：</label><label style='font-weight: bolder'>OQC</label>
            </a-col>
          </a-row>
          <line-chart class='cop-chart' id='elcReport' :show-split-line='true' :x-axis='elcChartLegend'
                      :series-data='elcChartSeries' :y-axis='yAxis' />
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
        operation: '',
        spec: ''
      },
      formRules: {
        startTime: [
          { required: true, message: '请输入起始时间' }
        ],
        endTime: [
          { required: true, message: '请输入起始时间' }
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
            this.areaTagList = 'PEMS_LCHW_ColdCapacityT.VAL_Actl,PEMS_MCHW_ColdCapacityT.VAL_Actl,PEMS_RCHW_ColdCapacityT.VAL_Actl'
            break
          case '1':
            this.areaTagList = 'PEMS_LCHW_ColdCapacity1T.VAL_Actl,PEMS_MCHW_ColdCapacity1T.VAL_Actl,PEMS_RCHW_ColdCapacity1T.VAL_Actl'
            break
          case '2':
            this.areaTagList = 'PEMS_LCHW_ColdCapacity2T.VAL_Actl,PEMS_MCHW_ColdCapacity2T.VAL_Actl,PEMS_RCHW_ColdCapacity2T.VAL_Actl'
            break
          default:
            break
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
      this.loading = true
      this.waterUseChartLegend = ['2021-04-28', '2021-04-29', '2021-04-30', '2021-05-01', '2021-05-02', '2021-05-03', '2021-05-04', '2021-05-05', '2021-05-06', '2021-05-07']
      this.waterUseChartSeries = [
        { name: 'CUT', data: [482, 568, 652, 356, 289, 980, 886, 756, 658, 589] },
        { name: 'BINDING', data: [258, 369, 458, 425, 436, 520, 555, 612, 602, 458] }
      ]
      this.loading = false
    },
    handlerCOPChartSubmit() {
      this.loading = true
      this.COPChartLegend = ['2021-04-28', '2021-04-29', '2021-04-30', '2021-05-01', '2021-05-02', '2021-05-03', '2021-05-04', '2021-05-05', '2021-05-06', '2021-05-07']
      this.COPChartSeries = [
        { name: 'A班[8:30 - 20:30]', data: [200, 320, 652, 569, 653, 589, 658, 486, 785, 720] },
        { name: 'B班[20:30 - 8:30]', data: [258, 256, 458, 425, 436, 520, 485, 389, 458, 458] }
      ]
      this.loading = false
    },
    handlerElcChartSubmit() {
      this.loading = true
      this.elcChartLegend = ['2021-04-28', '2021-04-29', '2021-04-30', '2021-05-01', '2021-05-02', '2021-05-03', '2021-05-04', '2021-05-05', '2021-05-06', '2021-05-07']
      this.elcChartSeries = [
        { name: '谢青春', data: [200, 320, 452, 569, 653, 625, 590, 653, 635, 720] }
      ]
      this.loading = false
    }
  }
}
</script>

<style lang='less' scoped>
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