<template>
  <a-modal
    :visible="visible"
    :title="currentEqp['NAME']"
    :footer="null"
    :destroyOnClose="true"
    width="700px"
    @cancel="handlerCancel"
  >
    <a-descriptions title="设备实时信息" layout="vertical">
      <a-descriptions-item label="设备名称">
        <label style="color: #409cff; font-weight: bold">{{ currentEqp['DESCRIPTION'] || '未知' }}</label>
      </a-descriptions-item>
      <a-descriptions-item label="锁定状态">
        <label style="color: #409cff; font-weight: bold">{{ currentEqp['MACHINE_HOLD_STATE'] || '未知' }}</label>
      </a-descriptions-item>
      <a-descriptions-item label="工段">
        <label style="color: #409cff; font-weight: bold">{{ currentEqp['GROUP_NAME'] || '未知' }}</label>
      </a-descriptions-item>
      <a-descriptions-item label="模式">
        <label style="color: #409cff; font-weight: bold">{{ currentEqp['AUTO_MODE'] || '未知' }}</label>
      </a-descriptions-item>
      <a-descriptions-item label="当前工单">
        <label style="color: #409cff; font-weight: bold">{{ currentEqp['PRODUCT_REQUEST_NAME'] || '未知' }}</label>
      </a-descriptions-item>
      <a-descriptions-item label="Recipe">
        <label style="color: #409cff; font-weight: bold">{{ currentEqp['RECIPE_NAME'] || '未知' }}</label>
      </a-descriptions-item>
    </a-descriptions>
    <a-divider />
    <a-descriptions title="设备实时Alarm" />
    <div class="alarm-table-div">
      <a-table
        ref="xTTTSTable"
        :rowKey="alarmData['SET_TIME']"
        :loading="alarmLoading"
        :columns="columns"
        :data-source="alarmData"
        :pagination="false"
      >
          <span slot="level" slot-scope="level">
            <a-tag :color="level === 'Serious' ? 'red' : level==='Warning' ? 'yellow' : 'green'"
            >
              {{ level }}
            </a-tag>
          </span>
      </a-table>
    </div>
  </a-modal>
</template>

<script>
export default {
  name: 'MacInfoModal',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    currentEqp: {
      type: Object,
      default: {}
    },
    alarmData: {
      type: Array,
      default: () => {return []}
    }
  },
  data() {
    return {
      alarmLoading: false,
      columns: [
        { title: 'ALARM_ID', key: this.changeUL('ALARM_ID'), dataIndex: this.changeUL('ALARM_ID'), align: 'center'},
        { title: 'SET_TIME', key: this.changeUL('SET_TIME'), dataIndex: this.changeUL('SET_TIME'), align: 'center',
          sorter: (a, b) => new Date(a[this.changeUL('SET_TIME')]).getTime() - new Date(b[this.changeUL('SET_TIME')]).getTime(),
          sortDirections: ['descend', 'ascend']
        },
        { title: 'ALARM_TEXT', key: this.changeUL('ALARM_TEXT'), dataIndex: this.changeUL('ALARM_TEXT')},
        { title: 'ALARM_LEVEL', key: this.changeUL('ALARM_LEVEL'), dataIndex: this.changeUL('ALARM_LEVEL'), scopedSlots: { customRender: 'level' }, align: 'center'}
      ]
    }
  },
  methods: {
    changeUL(str) {
      return this.changeUpperOrLower(str)
    },
    handlerCancel() {
      this.$emit('close', null)
    }
  }
}
</script>

<style lang="less" scoped>
.alarm-table-div {
  height: 320px;
  overflow-y: auto;

  /deep/ .ant-table-thead > tr > th, .ant-table-tbody > tr > td {
    padding: 8px;
  }
}
</style>