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
        >
          <vxe-form-item span="5" title="开始时间" field="startTime" :item-render="{}">
            <template #default>
              <vxe-input v-model="form.startTime" placeholder="日期选择" type="date"></vxe-input>
            </template>
          </vxe-form-item>
          <vxe-form-item span="5" title="结束时间" field="endTime" :item-render="{}">
            <template #default>
              <vxe-input v-model="form.endTime" placeholder="日期选择" type="date"></vxe-input>
            </template>
          </vxe-form-item>
          <vxe-form-item span="5" title="设备" field="flow">
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
      <a-card title="各设备状态分布">
        <div id="MacStateChart" :style="{height: (height * 0.8)+'px'}" />
      </a-card>
    </a-spin>
    <a-row :gutter="6">
      <a-col :span="6" v-for="card in cardSource" :key="cardSource.indexOf(card)">
        <a-card style="height: 190px">
          <a-descriptions :title="card['NAME']" layout="vertical">
            <a-descriptions-item label="稼动时长">
              <label style="color: #409cff; font-weight: bold">{{ card['RUN_DURATION'] + card['IDLE_DURATION'] }} H</label>
            </a-descriptions-item>
            <a-descriptions-item label="宕机时长">
              <label style="color: #ff7878; font-weight: bold">{{ card['DOWN_DURATION'] }} H</label>
            </a-descriptions-item>
            <a-descriptions-item label="饱和度">
              {{ ((card['RUN_DURATION'] + card['IDLE_DURATION']) / card['T_DURATION']).toFixed(2) * 100 }} %
            </a-descriptions-item>
          </a-descriptions>
          <state-duration-bar
            :run="card['RUN_DURATION']"
            :idle="card['IDLE_DURATION']"
            :pm="card['PM_DURATION']"
            :stop="card['STOP_DURATION']"
            :trouble="card['DOWN_DURATION']"
            :total="card['T_DURATION']"
          />
        </a-card>
      </a-col>
    </a-row>
    <a-modal
      v-model="visibleOfAlarmModal"
      title="相关Alarm"
      width="950px"
      :bodyStyle="{padding: '5px', height: '600px'}"
      :footer="null"
      centered
    >
      <a-table
        ref="xAlarmTable"
        :loading="alarmLoading"
        :columns="columns"
        :data-source="datasource"
        :pagination="false"
      >
        <span slot="level" slot-scope="level">
          <a-tag :color="level === 'Serious' ? 'red' : level==='Warning' ? 'yellow' : 'green'"
          >
            {{ level }}
          </a-tag>
        </span>
      </a-table>
    </a-modal>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { formatDate, getObjArrayFieldToArray, getCurrentTime } from '@/utils/util'
import { executeSQL } from '@api/api'
import QuerySelect from '@comp/QuerySelect'
import DataTable from '@comp/DataTable'
import StateDurationBar from '@views/report/components/StateDurationBar'

export default {
  name: 'MachineStateChart',
  components: { StateDurationBar, DataTable, QuerySelect },
  computed: {
    height() {
      return this.$store.getters.bodyHeight - 135
    }
  },
  data() {
    return {
      form: {
        startTime: getCurrentTime('date', -7, 'day'),
        endTime: getCurrentTime('date'),
        machine: ''
      },
      formRules: {
        startTime: [
          { required: true, message: '请输入起始时间' }
        ],
        endTime: [
          { required: true, message: '请输入起始时间' }
        ]
      },
      loading: false,
      alarmLoading: false,
      data: [],
      types: [
        { name: 'Trouble', color: '#ff6565' },
        { name: 'Run', color: '#a1ff9a' },
        { name: 'Idle', color: '#fff86c' },
        { name: 'Maintenance', color: '#6ca8ff' },
        { name: 'Stop', color: '#f56cff' }
      ],
      startTime: new Date().getTime(),
      categories: [],
      macSource: { options: [] },
      macStateSource: [],
      visibleOfAlarmModal: false,
      datasource: [],
      columns: [
        { title: 'ALARM_ID', key: this.changeUL('ALARM_ID'), dataIndex: this.changeUL('ALARM_ID'), align: 'center'},
        { title: 'UNIT_NAME', key: this.changeUL('UNIT_NAME'), dataIndex: this.changeUL('UNIT_NAME'), align: 'center'},
        { title: 'SET_TIME', key: this.changeUL('SET_TIME'), dataIndex: this.changeUL('SET_TIME'), align: 'center',
          sorter: (a, b) => new Date(a[this.changeUL('SET_TIME')]).getTime() - new Date(b[this.changeUL('SET_TIME')]).getTime(),
          sortDirections: ['descend', 'ascend']
        },
        { title: 'ALARM_TEXT', key: this.changeUL('ALARM_TEXT'), dataIndex: this.changeUL('ALARM_TEXT')},
        { title: 'ALARM_CODE', key: this.changeUL('ALARM_CODE'), dataIndex: this.changeUL('ALARM_CODE'), align: 'center'},
        { title: 'ALARM_LEVEL', key: this.changeUL('ALARM_LEVEL'), dataIndex: this.changeUL('ALARM_LEVEL'), scopedSlots: { customRender: 'level' }, align: 'center'}
      ],
      cardSource: []
    }
  },
  mounted() {
    this.initData()
  },
  methods: {
    async initData() {
      this.loading = true
      let params = this.form
      params['sql_name'] = 'getMacStateDuration'
      let res_macState = await executeSQL(params)
      if(res_macState['success']) {
        this.macStateSource = res_macState['result']
      }

      params['sql_name'] = 'getAllNormalMachine'
      let res_categories = await executeSQL(params)
      if(res_categories['success']) {
        this.macSource['options'] = res_categories['result']
        this.categories = getObjArrayFieldToArray(res_categories['result'], 'NAME')
      }

      params['sql_name'] = 'getEveryStateDuration'
      let res_state_duration = await executeSQL(params)
      if(res_state_duration['success']) {
        this.cardSource = res_state_duration['result']
        console.log(this.cardSource)
      }

      this.renderChart()
      this.loading = false
    },
    renderChart() {
      let that = this
      that.initLineChart()
    },
    initLineChart() {
      let that = this
      const id = 'MacStateChart'
      if (document.getElementById(id)) {
        echarts.dispose(document.getElementById(id))
        that.chart = echarts.init(document.getElementById(id))
        that.chart.clear()

        that.startTime = that.macStateSource[0]['TIMEKEY']
        that.macStateSource.forEach(mac => {
          let typeItem = that.types.filter(type => {return type['name']===mac['OLD_MACHINE_STATE_NAME']})[0];
          that.data.push({
            name: typeItem.name,
            value: [this.categories.indexOf(mac['NAME']), mac['TIMEKEY'], mac['LEAD_TIMEKEY'], mac['LEAD_TIMEKEY'] - mac['TIMEKEY']],
            itemStyle: {
              normal: {
                color: typeItem.color
              }
            }
          });
        })

        function renderItem(params, api) {
          let categoryIndex = api.value(0);
          let start = api.coord([api.value(1), categoryIndex]);
          let end = api.coord([api.value(2), categoryIndex]);
          let height = api.size([0, 1])[1] * 0.6;
          let rectShape = echarts.graphic.clipRectByRect(
            {
              x: start[0],
              y: start[1] - height / 2,
              width: end[0] - start[0],
              height: height
            },
            {
              x: params.coordSys.x,
              y: params.coordSys.y,
              width: params.coordSys.width,
              height: params.coordSys.height
            }
          );
          return (
            rectShape && {
              type: 'rect',
              transition: ['shape'],
              shape: rectShape,
              style: api.style()
            }
          );
        }

        let optionData = {
          tooltip: {
            formatter: function (params) {
              return params.marker + params.name + ': ' + (params.value[3]/60/1000/60).toFixed(2) + ' 小时<br/>'
                + formatDate(params.value[1], 'yyyy-MM-dd hh:mm:ss')
                + ' ~ '
                + formatDate(params.value[2], 'yyyy-MM-dd hh:mm:ss');
            }
          },
          legend: {//图例
            data: [
              {name: 'Run', itemStyle: { color: '#a1ff9a' }},
              {name: 'Idle', itemStyle: { color: '#fff86c' }},
              {name: 'Stop', itemStyle: { color: '#f56cff' }},
              {name: 'Maintenance(PM)', itemStyle: { color: '#6ca8ff' }},
              {name: 'Trouble', itemStyle: { color: '#ff6565' }}
            ],
            left: '50px',
            selectedMode: false, // 图例设为不可点击,
            textStyle: {
              color: '#000000',
              fontSize:14
            }
          },
          dataZoom: [
            {
              type: 'slider',
              filterMode: 'weakFilter',
              showDataShadow: false,
              labelFormatter: ''
            },
            {
              type: 'inside',
              filterMode: 'weakFilter'
            }
          ],
          xAxis: {
            min: that.startTime,
            scale: true,
            axisLabel: {
              formatter: function (val) {
                return formatDate(val, 'yyyy-MM-dd hh:mm:ss');
              }
            }
          },
          yAxis: {
            data: that.categories
          },
          series: [
            { name: "Run", type: 'bar', data: [],barMaxWidth:10 },
            { name: "Idle", type: 'bar', data: [],barMaxWidth:10 },
            { name: "Stop", type: 'bar', data: [],barMaxWidth:10 },
            { name: "Maintenance(PM)", type: 'bar', data: [],barMaxWidth:10 },
            { name: "Trouble", type: 'bar', data: [],barMaxWidth:10 },
            {
              type: 'custom',
              renderItem: renderItem,
              itemStyle: {
                opacity: 1
              },
              encode: {
                x: [1, 2],
                y: 0
              },
              data: that.data
            }
          ]
        }

        that.chart.setOption(optionData)
        that.chart.on('click', (params) => {
          this.alarmLoading = true
          let params_search = {}
          that.visibleOfAlarmModal = true
          params_search['machine'] = this.categories[params.value[0]]
          params_search['startTime'] = formatDate(params.value[1] - 1000*60*5, 'yyyy-MM-dd hh:mm:ss')
          params_search['endTime'] = formatDate(params.value[2], 'yyyy-MM-dd hh:mm:ss')
          params_search['sql_name'] = 'getMacAlarmParams'
          executeSQL(params_search).then(res => {
            if(res['success']) {
              this.datasource = res['result']
              this.alarmLoading = false
            }
          })
        })
        window.addEventListener('resize', function() {
          if (!that.chart.isDisposed()) {
            that.chart.resize()
          }
        })
      }
    },
    handlerSubmit() {
      this.initData()
    },
    changeUL(str) {
      return this.changeUpperOrLower(str)
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
</style>