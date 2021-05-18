<template>
  <div class="main-div">
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
          <vxe-form-item span="5" title="开始时间" field="startTime">
            <vxe-input v-model="form.startTime" placeholder="日期选择" type="date"></vxe-input>
          </vxe-form-item>
          <vxe-form-item span="5" title="结束时间" field="endTime">
            <vxe-input v-model="form.endTime" placeholder="日期选择" type="date"></vxe-input>
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
    <a-card title="能源分配图">
      <sankey-chart
        id="sankeyEnergyConsumChart"
        :style="{width: '100%', height: height + 'px'}"
        :nodes="sankeyData.nodes"
        :links="sankeyData.links"
      />
    </a-card>
  </div>
</template>

<script>
import SankeyChart from '@comp/chart/SankeyChart'
import { getRangeEnergyConsume } from '@api/energyApi'

let tagList = [
  'Total_Capacity',
  'PEMS_LCHW_ColdCapacityT',
  'PEMS_MCHW_ColdCapacityT',
  'PEMS_RCHW_ColdCapacityT',
  'PEMS_LCHW_ColdCapacity1T',
  'PEMS_MCHW_ColdCapacity1T',
  'PEMS_RCHW_ColdCapacity1T',
  'PEMS_LCHW_ColdCapacity2T',
  'PEMS_MCHW_ColdCapacity2T',
  'PEMS_RCHW_ColdCapacity2T'
]

let linkList = [
  { tag: 'PEMS_LCHW_ColdCapacityT', parent: 'Total_Capacity' },
  { tag: 'PEMS_MCHW_ColdCapacityT', parent: 'Total_Capacity' },
  { tag: 'PEMS_RCHW_ColdCapacityT', parent: 'Total_Capacity' },
  { tag: 'PEMS_LCHW_ColdCapacity1T', parent: 'PEMS_LCHW_ColdCapacityT' },
  { tag: 'PEMS_MCHW_ColdCapacity1T', parent: 'PEMS_MCHW_ColdCapacityT' },
  { tag: 'PEMS_RCHW_ColdCapacity1T', parent: 'PEMS_RCHW_ColdCapacityT' },
  { tag: 'PEMS_LCHW_ColdCapacity2T', parent: 'PEMS_LCHW_ColdCapacityT' },
  { tag: 'PEMS_MCHW_ColdCapacity2T', parent: 'PEMS_MCHW_ColdCapacityT' },
  { tag: 'PEMS_RCHW_ColdCapacity2T', parent: 'PEMS_RCHW_ColdCapacityT' }
]

let valueList = [
  { tag: 'PEMS_LCHW_ColdCapacityT', value: '47' },
  { tag: 'PEMS_MCHW_ColdCapacityT', value: '5045' },
  { tag: 'PEMS_RCHW_ColdCapacityT', value: '172' },
  { tag: 'PEMS_LCHW_ColdCapacity1T', value: '12' },
  { tag: 'PEMS_MCHW_ColdCapacity1T', value: '456' },
  { tag: 'PEMS_RCHW_ColdCapacity1T', value: '47' },
  { tag: 'PEMS_LCHW_ColdCapacity2T', value: '35' },
  { tag: 'PEMS_MCHW_ColdCapacity2T', value: '4589' },
  { tag: 'PEMS_RCHW_ColdCapacity2T', value: '125' }
]

export default {
  name: 'EnergyDistributionReport',
  components: { SankeyChart },
  computed: {
    height() {
      return this.$store.getters.bodyHeight - 315
    }
  },
  data() {
    return {
      form: {
        startTime: '',
        endTime: ''
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
      sankeyData: {
        nodes: [],
        links: []
      }
    }
  },
  methods: {
    handlerSubmit() {
      this.refreshData()
    },
    refreshData() {
      let params = this.form
      params['tagList'] = '' +
        'PEMS_LCHW_ColdCapacityT.VAL_Actl,' +
        'PEMS_MCHW_ColdCapacityT.VAL_Actl,' +
        'PEMS_RCHW_ColdCapacityT.VAL_Actl,' +
        'PEMS_LCHW_ColdCapacity1T.VAL_Actl,' +
        'PEMS_MCHW_ColdCapacity1T.VAL_Actl,' +
        'PEMS_RCHW_ColdCapacity1T.VAL_Actl,' +
        'PEMS_LCHW_ColdCapacity2T.VAL_Actl,' +
        'PEMS_MCHW_ColdCapacity2T.VAL_Actl,' +
        'PEMS_RCHW_ColdCapacity2T.VAL_Actl'
      getRangeEnergyConsume(params).then(res => {
        if(res && res.length > 0) {
          valueList = res
        }
        this.refreshSankeyChart()
      })
    },
    refreshSankeyChart() {
      let links_temp = []
      this.sankeyData.nodes = tagList
      linkList.forEach(li => {
        let link = {
          source: li['parent'],
          target: li['tag'],
          value: this.getValueOfTag(li['tag'])
        }
        links_temp.push(link)
      })
      this.sankeyData.links = links_temp
    },
    getValueOfTag(tag_name) {
      for (let i = 0; i < valueList.length; i++) {
        if(tag_name === valueList[i]['tag']){
          return valueList[i]['value']
        }
      }
    }
  }
}
</script>

<style lang="less" scoped>
.params-panel {
  padding: 6px 12px;
  background: #FFFFFF;
  border-radius: 4px;
  margin-bottom: 6px;
}
</style>