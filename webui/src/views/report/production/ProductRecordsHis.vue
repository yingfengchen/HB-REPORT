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
          <vxe-form-item span="5" title="开始时间" field="startTime" :item-render="{}" title-overflow="ellipsis">
            <template #default>
              <vxe-input v-model="form.startTime" placeholder="时间选择" type="datetime" clearable />
            </template>
          </vxe-form-item>
          <vxe-form-item span="5" title="结束时间" field="endTime" :item-render="{}" title-overflow="ellipsis">
            <template #default>
              <vxe-input v-model="form.endTime" placeholder="时间选择" type="datetime"></vxe-input>
            </template>
          </vxe-form-item>
          <vxe-form-item span="5" title="产品ID" field="product" :item-render="{}" title-overflow="ellipsis">
            <template #default>
              <vxe-input v-model="form.product" placeholder="请输入产品ID" type="text"></vxe-input>
            </template>
          </vxe-form-item>
          <vxe-form-item span="5" title="站点" field="operation" :item-render="{}" title-overflow="ellipsis">
            <template #default>
              <query-select
                ref="QSOfPH"
                v-model="form.operation"
                url="/common/executeSql"
                method="post"
                :params="{sql_name: 'getAllOperations'}"
                :option-config="{label: changeUL('description'), value: changeUL('name')}"
              />
            </template>
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
        title="产品信息表"
        :height="height"
        :columns="columns"
        :datasource="datasource"
      />
    </a-spin>
  </div>
</template>

<script>
import BarChart from '@comp/chart/BarChart'
import QuerySelect from '@comp/QuerySelect'
import LineChart from '@comp/chart/LineChart'
import DataTable from '@comp/DataTable'
import { postAction } from '@api/manage'

export default {
  name: 'DefectRecordsReport',
  components: {
    DataTable,
    LineChart,
    QuerySelect,
    BarChart
  },
  computed: {
    height() {
      return this.$store.getters.bodyHeight - 175
    }
  },
  data() {
    return {
      form: {
        startTime: '',
        endTime: '',
        product: '',
        operation: ''
      },
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
        { title: '时间戳', field: this.changeUL('timekey'), align: 'center', sortable: true, width: 150 },
        { title: '产品 ID', field: this.changeUL('name'), align: 'center', sortable: true, width: 150 },
        { title: '工厂', field: this.changeUL('factory_name'), align: 'center', sortable: true, width: 150 },
        { title: '返厂次数', field: this.changeUL('fab_in_count'), align: 'center', width: 150 },
        { title: '事件名称', field: this.changeUL('last_event_name'), align: 'center', width: 150 },
        { title: '事件时间', field: this.changeUL('last_event_time'), align: 'center', width: 150 },
        { title: '事件操作员', field: this.changeUL('last_event_user'), align: 'center', width: 150 },
        { title: '不良描述', field: this.changeUL('defect_description'), align: 'center', width: 150 },
        { title: '创建时间', field: this.changeUL('create_time'), align: 'center', width: 150 },
        { title: '创建者', field: this.changeUL('create_user'), align: 'center', width: 150 },
        { title: '产品状态', field: this.changeUL('product_state'), align: 'center', width: 150 },
        { title: '工艺状态', field: this.changeUL('process_state'), align: 'center', width: 150 },
        { title: '产品种类', field: this.changeUL('product_spec_name'), align: 'center', width: 150 },
        { title: '所在站点', field: this.changeUL('process_operation_name'), align: 'center', width: 150 },
        { title: '所属工单', field: this.changeUL('product_request_name'), align: 'center', width: 150 },
        { title: '等级', field: this.changeUL('grade'), align: 'center', width: 150 },
        { title: '不良代码', field: this.changeUL('fg_code'), align: 'center', width: 150 },
        { title: 'Hold 状态', field: this.changeUL('hold_state'), align: 'center', width: 150 },
        { title: '入厂时间', field: this.changeUL('initial_trackin_time'), align: 'center', sortable: true, width: 150 },
        { title: '判定结果', field: this.changeUL('judge'), align: 'center', width: 150 },
        { title: '返工次数', field: this.changeUL('rework_count'), align: 'center', width: 150 }
      ],
      datasource: []
    }
  },
  methods: {
    handlerSubmit() {
      this.handlerWaterUseChartSubmit()
    },
    handlerWaterUseChartSubmit() {
      let _this = this
      let params = _this.form
      params['sql_name'] = 'getProductRecordsByParams'

      postAction('/common/executeSql', params).then(res => {
        _this.datasource = res['result']
      }).catch(error => {
        this.$message.error(error)
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