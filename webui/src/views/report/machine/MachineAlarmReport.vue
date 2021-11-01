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
          @reset="handleReset"
        >
          <vxe-form-item span="5" title="发生时间段" field="startTime" :item-render="{}" title-overflow="ellipsis">
            <template #default>
              <vxe-input v-model="form.startTime" placeholder="时间选择" type="datetime" clearable />
            </template>
          </vxe-form-item>
          <vxe-form-item span="5" title="~" field="endTime" :item-render="{}" title-overflow="ellipsis">
            <template #default>
              <vxe-input v-model="form.endTime" placeholder="时间选择" type="datetime"></vxe-input>
            </template>
          </vxe-form-item>
          <vxe-form-item span="5" title="设备单元" field="machine">
            <query-select
              ref="xLine"
              v-model="form.machine"
              :options="macSource"
              :option-config="{label: changeUL('name'), value: changeUL('name')}"
            />
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
      <data-table
        :loading="loading"
        title="报警数据列表"
        :height="height"
        :columns="columns"
        :datasource="datasource"
        :can-export="true"
      />
    </a-spin>
  </div>
</template>

<script>
import QuerySelect from '@comp/QuerySelect'
import DataTable from '@comp/DataTable'
import { executeSQL } from '@api/api'
import { postAction } from '@api/manage'
const { getCurrentTime } = require('@/utils/util')

export default {
  name: 'MachineAlarmReport',
  components: {
    DataTable,
    QuerySelect
  },
  computed: {
    height() {
      return this.$store.getters.bodyHeight - 175
    }
  },
  data() {
    return {
      form: {
        startTime: getCurrentTime('date', -3, 'day') + ' 08:30:00',
        endTime: getCurrentTime('date') + ' 08:30:00',
        machine: ''
      },
      macSource: { options: [] },
      formRules: {
        'startTime': [
          { required: true }
        ],
        'endTime': [
          { required: true }
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
      areaTagList: 'PEMS_LCHW_ColdCapacityT.VAL_Actl,PEMS_MCHW_ColdCapacityT.VAL_Actl,PEMS_RCHW_ColdCapacityT.VAL_Actl',
      columns: [
        { title: 'ALARM_ID', field: this.changeUL('ALARM_ID'), align: 'center', sortable: true, width: 150 },
        { title: 'MACHINE_NAME', field: this.changeUL('MACHINE_NAME'), align: 'center', sortable: true, width: 150 },
        { title: 'SET_TIME', field: this.changeUL('SET_TIME'), align: 'center', sortable: true, width: 150 },
        { title: 'ALARM_STATE', field: this.changeUL('ALARM_STATE'), align: 'center', width: 150 },
        { title: 'CLEAR_TIME', field: this.changeUL('CLEAR_TIME'), align: 'center', width: 150 },
        { title: 'CLEAR_USER', field: this.changeUL('CLEAR_USER'), align: 'center', width: 150 },
        { title: 'ALARM_TEXT', field: this.changeUL('ALARM_TEXT'), align: 'center', width: 300 },
        { title: 'ALARM_CODE', field: this.changeUL('ALARM_CODE'), align: 'center', width: 150 },
        { title: 'ALARM_LEVEL', field: this.changeUL('ALARM_LEVEL'), align: 'center', width: 150},
        { title: 'UNIT_NAME', field: this.changeUL('UNIT_NAME'), align: 'center', width: 150}
      ],
      datasource: []
    }
  },
  mounted() {
    this.initData()
  },
  methods: {
    async initData() {
      let params = this.form

      params['sql_name'] = 'getAllNormalMachine'
      let res_categories = await executeSQL(params)
      if(res_categories['success']) {
        this.macSource['options'] = res_categories['result']
      }
    },
    handlerSubmit() {
      this.handlerWaterUseChartSubmit()
    },
    handlerWaterUseChartSubmit() {
      this.loading = true
      let _this = this
      let params = JSON.parse(JSON.stringify(_this.form))
      params['sql_name'] = 'getMacAlarmParams'

      postAction('/common/executeSql', params).then(res => {
        _this.datasource = res['result']
        _this.loading = false
      }).catch(error => {
        _this.$message.error(error)
        _this.loading = false
      })
    },
    handleReset() {
      this.$refs.QSOfPH.clear()
    },
    changeUL(str) {
      return this.changeUpperOrLower(str)
    }
  }
}
</script>

<style lang="less" scoped>
.trend-of-water-use-div {
  margin: -6px -12px 0 0;

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