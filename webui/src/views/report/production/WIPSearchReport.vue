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
          <vxe-form-item span='5' title='Lot ID' field='machine'>
            <vxe-input placeholder='请输入' type='text'></vxe-input>
          </vxe-form-item>
          <vxe-form-item span='5' title='Product ID' field='operator'>
            <vxe-input placeholder='请输入' type='text'></vxe-input>
          </vxe-form-item>
          <vxe-form-item span='5' title='站点' field='operator'>
            <a-select
              :maxTagCount='2'
              :maxTagTextLength='3'
            >
              <a-select-option key='1'>CUT</a-select-option>
              <a-select-option key='2'>BINDING</a-select-option>
              <a-select-option key='3'>BENDING</a-select-option>
            </a-select>
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
      <a-row>
        <a-col :span='12'>
          <a-card title='各站点产品堆积情况'>
            <div :style="{height: (height * 0.5)+'px'}">
              <a-row>
                <a-col :span='12'>
                  <a-radio-group :value='switchWaterUseUnit' @change='handlerWaterUseUnitChange'>
                    <a-radio-button value='%Y-%m-%d %H'>Line1</a-radio-button>
                    <a-radio-button value='%Y-%m-%d'>Line2</a-radio-button>
                  </a-radio-group>
                </a-col>
              </a-row>
              <line-chart class='cold-capacity-chart' id='trendWaterUse' :show-split-line='true'
                          :x-axis='waterUseChartLegend'
                          :series-data='waterUseChartSeries' :y-axis='yAxis' />
            </div>
          </a-card>
        </a-col>
        <a-col :span='12'>
        <a-card title='各产品规格占比'>
          <div :style="{height: (height * 0.5)+'px'}">
            <pie-chart id='PersonErrorPie' :datasource='PieDatasource' :legend-list='PieLegend' :total-value='879' unit='片' text='总产品数'></pie-chart>
          </div>
        </a-card>
      </a-col>
      </a-row>
      <data-table
        title='在制 Product 列表'
        :height='890'
        :columns='columns'
        :datasource='datasource'
      />
    </a-spin>
  </div>
</template>

<script>
import BarChart from '@comp/chart/BarChart'
import QuerySelect from '@comp/QuerySelect'
import LineChart from '@comp/chart/LineChart'
import { getCumulative, getInstantaneous } from '@api/energyApi'
import { transferStringToArray } from '@/utils/util'
import DataTable from '@comp/DataTable'
import PieChart from '@comp/chart/PieChart'

export default {
  name: 'WIPSearchReport',
  components: {
    PieChart,
    LineChart,
    QuerySelect,
    BarChart,
    DataTable
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
        machine: '',
        operator: ''
      },
      formRules: {},
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
          name: '片数',
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
      areaTagList: 'PEMS_LCHW_ColdCapacityT.VAL_Actl,PEMS_MCHW_ColdCapacityT.VAL_Actl,PEMS_RCHW_ColdCapacityT.VAL_Actl',
      columns: [
        { title: '产品ID', field: 'ID', align: 'center', sortable: true },
        { title: '所在站点', field: 'Name', align: 'center' },
        { title: '所属Lot', field: 'suplyer', align: 'center' },
        { title: '所属工单', field: 'productionDate', align: 'center' },
        { title: '等级', field: 'inWMSDate', align: 'center' },
        { title: '不良Code', field: 'reciver', align: 'center' },
        { title: '是否返工', field: 'useTime', align: 'center' },
        { title: '上次操作时间', field: 'productionID', align: 'center', sortable: true },
        { title: '上次操作事件', field: 'desc', align: 'center' }
      ],
      datasource: [],
      PieDatasource: [],
      PieLegend: []
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
    },
    handlerWaterUseChartSubmit() {
      this.loading = true
      this.waterUseChartLegend = ['C011', 'C012', 'OQ01', 'C013', 'C014', 'C015', 'C016', 'OQ02', 'S001']
      this.waterUseChartSeries = [
        { name: '当前产品数量', data: [120, 24, 95, 21, 15, 16, 56, 122, 8, 6] }
      ]
      this.datasource = [
        {
          ID: 'EZ2021042126',
          Name: 'C011',
          productionDate: 'WO2021030521',
          suplyer: 'TY20210415124568',
          inWMSDate: 'A',
          reciver: '',
          useTime: 'Y',
          productionID: '2021-04-30 14:30:45',
          desc: 'TrackIn'
        },
        {
          ID: 'EZ2021031802',
          Name: 'C011',
          productionDate: 'WO2021041845',
          suplyer: 'TY20210418525898',
          inWMSDate: 'A',
          reciver: '',
          useTime: 'N',
          productionID: '2021-04-30 14:32:25',
          desc: 'TrackIn'
        },
        {
          ID: 'EZ2021050578',
          Name: 'C011',
          productionDate: 'WO2021041635',
          suplyer: 'TY20210501458974',
          inWMSDate: 'A',
          reciver: '',
          useTime: 'N',
          productionID: '2021-04-30 14:35:23',
          desc: 'TrackOut'
        },
        {
          ID: 'EZ2021042174',
          Name: 'C011',
          productionDate: 'WO2021041635',
          suplyer: 'TY20210415425898',
          inWMSDate: 'A',
          reciver: '',
          useTime: 'N',
          productionID: '2021-04-29 14:36:33',
          desc: 'TrackIn'
        },
        {
          ID: 'EZ2021031952',
          Name: 'C011',
          productionDate: 'WO2021041635',
          suplyer: 'TY20210419533589',
          inWMSDate: 'A',
          reciver: '',
          useTime: 'N',
          productionID: '2021-05-07 14:38:45',
          desc: 'TrackIn'
        },
        {
          ID: 'EZ2021050325',
          Name: 'C011',
          productionDate: 'WO2021030521',
          suplyer: 'TY20210501258967',
          inWMSDate: 'C',
          reciver: 'E012',
          useTime: 'Y',
          productionID: '2021-05-06 14:42:56',
          desc: 'TrackOut'
        },
        {
          ID: 'EZ2021042126',
          Name: 'C011',
          productionDate: 'WO2021041845',
          suplyer: 'TY20210501258975',
          inWMSDate: 'A',
          reciver: '',
          useTime: 'N',
          productionID: '2021-05-08 14:45:20',
          desc: 'TrackIn'
        },
        {
          ID: 'EZ2021042126',
          Name: 'C011',
          productionDate: 'WO2021041845',
          suplyer: 'TY20210430145896',
          inWMSDate: 'A',
          reciver: '',
          useTime: 'N',
          productionID: '2021-05-08 14:47:58',
          desc: 'TrackIn'
        },
        {
          ID: 'EZ2021042126',
          Name: 'C011',
          productionDate: 'WO2021030521',
          suplyer: 'TY20210430145896',
          inWMSDate: 'A',
          reciver: '',
          useTime: 'N',
          productionID: '2021-05-08 14:47:58',
          desc: 'SetJudge'
        },
        {
          ID: 'EZ2021042126',
          Name: 'C011',
          suplyer: 'OQC01',
          productionDate: '2021-04-30',
          inWMSDate: '2021-05-01',
          reciver: '张旭',
          productionID: 'UYT2021050678',
          useTime: '2021-05-06 14:35:23'
        },
        {
          ID: 'EZ2021042126',
          Name: '李春梅',
          suplyer: 'OQC01',
          productionDate: '2021-04-30',
          inWMSDate: '2021-05-01',
          reciver: '张旭',
          productionID: 'UYT2021050619',
          useTime: '2021-05-06 14:36:33'
        },
        {
          ID: 'EZ2021042126',
          Name: '李春梅',
          suplyer: 'OQC01',
          productionDate: '2021-04-30',
          inWMSDate: '2021-05-01',
          reciver: '张旭',
          productionID: 'UYT2021050635',
          useTime: '2021-05-06 14:38:45'
        },
        {
          ID: 'EZ2021042126',
          Name: '李春梅',
          suplyer: '深圳龙翔材料有限公司',
          productionDate: '2021-04-30',
          inWMSDate: '2021-05-01',
          reciver: '张旭',
          productionID: 'UYT2021050625',
          useTime: '2021-05-06 14:42:56'
        },
        {
          ID: 'EZ2021042126',
          Name: '李春梅',
          suplyer: '深圳龙翔材料有限公司',
          productionDate: '2021-04-30',
          inWMSDate: '2021-05-01',
          reciver: '张旭',
          productionID: 'UYT2021050201',
          useTime: '2021-05-06 14:45:20'
        },
        {
          ID: 'EZ2021042126',
          Name: '李春梅',
          suplyer: '深圳龙翔材料有限公司',
          productionDate: '2021-04-30',
          inWMSDate: '2021-05-01',
          reciver: '张旭',
          productionID: 'UYT2021050256',
          useTime: '2021-05-06 14:47:58'
        },
        {
          ID: 'EZ2021042126',
          Name: '贴附薄膜',
          suplyer: '深圳龙翔材料有限公司',
          productionDate: '2021-04-30',
          inWMSDate: '2021-05-01',
          reciver: '张旭',
          productionID: 'UYT2021050678',
          useTime: '2021-05-06 14:35:23'
        },
        {
          ID: '78552687',
          Name: '贴附薄膜',
          suplyer: '深圳龙翔材料有限公司',
          productionDate: '2021-04-30',
          inWMSDate: '2021-05-01',
          reciver: '张旭',
          productionID: 'UYT2021050619',
          useTime: '2021-05-06 14:36:33'
        },
        {
          ID: '78552687',
          Name: '贴附薄膜',
          suplyer: '深圳龙翔材料有限公司',
          productionDate: '2021-04-30',
          inWMSDate: '2021-05-01',
          reciver: '张旭',
          productionID: 'UYT2021050635',
          useTime: '2021-05-06 14:38:45'
        },
        {
          ID: '78552687',
          Name: '贴附薄膜',
          suplyer: '深圳龙翔材料有限公司',
          productionDate: '2021-04-30',
          inWMSDate: '2021-05-01',
          reciver: '张旭',
          productionID: 'UYT2021050625',
          useTime: '2021-05-06 14:42:56'
        },
        {
          ID: '78552687',
          Name: '贴附薄膜',
          suplyer: '深圳龙翔材料有限公司',
          productionDate: '2021-04-30',
          inWMSDate: '2021-05-01',
          reciver: '张旭',
          productionID: 'UYT2021050201',
          useTime: '2021-05-06 14:45:20'
        },
        {
          ID: '78552687',
          Name: '贴附薄膜',
          suplyer: '深圳龙翔材料有限公司',
          productionDate: '2021-04-30',
          inWMSDate: '2021-05-01',
          reciver: '张旭',
          productionID: 'UYT2021050256',
          useTime: '2021-05-06 14:47:58'
        }
      ]
      this.PieDatasource = [
        {value: 335, name: 'FGY025846'},
        {value: 310, name: 'FGY078954'},
        {value: 234, name: 'FGY036548'}
      ]
      this.PieLegend = ['FGY025846', 'FGY078954', 'FGY036548']
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