<template>
  <div class="main-div">
    <a-row class="params-panel">
      <a-col :span="24">
        <vxe-form
          ref="xWaterUseForm"
          title-width="100px"
          :data="form"
          :rules="formRules"
          @submit="handlerSubmit"
        >
          <vxe-form-item span="5" title="标签ID" field="tag">
            <vxe-input v-model="form.tag" placeholder="请输入" type="text" clearable></vxe-input>
          </vxe-form-item>
          <vxe-form-item>
            <template #default>
              <vxe-button type="submit" status="primary">查询</vxe-button>
              <vxe-button type="reset">重置</vxe-button>
              <vxe-button @click="handlerAdd">添加</vxe-button>
              <vxe-button @click="handlerDelete">批量删除</vxe-button>
              <vxe-button @click="handlerImport">导入</vxe-button>
              <vxe-button @click="handlerExport">导出</vxe-button>
            </template>
          </vxe-form-item>
        </vxe-form>
      </a-col>
    </a-row>
    <data-table
      ref="xTagTable"
      title="标签列表"
      row-id="tag_name"
      :datasource="datasource"
      :height="height"
      :is-show-tool-bar="true"
      :columns="columns"
      :total-only="true"
      :page-size="datasource.length"
    />

    <a-modal
      title="添加节点"
      :visible="visible"
      @cancel="handleCancel"
    >
      <vxe-form ref="xInsertAdd" :data="addForm" :rules="addFormRules" :span="24" title-width="80" title-align="center" @submit="handlerInsert">
        <vxe-form-item title="标签ID" field="tag_name">
          <vxe-input v-model="addForm.tag_name" placeholder="请输入"></vxe-input>
        </vxe-form-item>
        <vxe-form-item title="能源名称" field="energy_name">
          <vxe-input v-model="addForm.energy_name" placeholder="请输入"></vxe-input>
        </vxe-form-item>
        <vxe-form-item title="父标签" field="source_name">
          <vxe-input v-model="addForm.source_name" placeholder="请输入"></vxe-input>
        </vxe-form-item>
        <vxe-form-item title="单位" field="value_unit">
          <vxe-input v-model="addForm.value_unit" placeholder="请输入"></vxe-input>
        </vxe-form-item>
        <vxe-form-item title="最小值" field="source_minspan" :span="12">
          <vxe-input v-model="addForm.source_minspan" placeholder="请输入"></vxe-input>
        </vxe-form-item>
        <vxe-form-item title="最大值" field="source_maxspan" :span="12">
          <vxe-input v-model="addForm.source_maxspan" placeholder="请输入"></vxe-input>
        </vxe-form-item>
        <vxe-form-item title="备注" field="remark">
          <vxe-input v-model="addForm.remark" placeholder="请输入"></vxe-input>
        </vxe-form-item>
      </vxe-form>

      <template slot="footer">
        <a-button key="back" @click="handleCancel">
          取消
        </a-button>
        <a-button type="primary" @click="handlerInsert">
          提交
        </a-button>
      </template>
    </a-modal>
  </div>
</template>

<script>
import DataTable from '@comp/DataTable'
import { deleteTags, getTagList, insertTags } from '@api/baseDataApi'

export default {
  name: 'tagManagement',
  components: { DataTable },
  computed: {
    height() {
      return this.$store.getters.bodyHeight - 175
    }
  },
  data() {
    return {
      form: {
        tag: ''
      },
      addForm: {
        tag_name: '',
        energy_name: '',
        source_name: '',
        value_unit: '',
        energy_type: '',
        source_minspan: '',
        source_maxspan: '',
        remark: ''
      },
      formRules: {},
      addFormRules: {
        tag_name: [
          { required: true, message: '必填项未填' }
        ]
      },
      datasource: [],
      columns: [
        { type: 'checkbox', width: 60, align: 'center' },
        { title: '标签ID', field: 'tag_name', align: 'center' },
        { title: '标签描述', field: 'energy_name', align: 'center' },
        { title: '父节点', field: 'source_name', align: 'center' },
        { title: '单位', field: 'value_unit', align: 'center' },
        { title: '能源类型', field: 'energy_type', align: 'center' },
        { title: '最小值', field: 'source_minspan', align: 'center' },
        { title: '最大值', field: 'source_maxspan', align: 'center' },
        { title: '备注', field: 'remark', align: 'center' }
      ],
      visible: false
    }
  },
  mounted() {
    this.handlerSubmit()
  },
  methods: {
    handlerSubmit() {
      let params = this.form
      getTagList(params).then(res => {
        this.datasource = res
      })
    },
    async handlerImport() {
      this.datasource = []
      await this.$refs.xTagTable.importData()
      insertTags(this.$refs.xTagTable.getTableData()).then(res => {
        this.handlerSubmit()
      })
    },
    handlerExport() {
      this.$refs.xTagTable.exportTableData()
    },
    handlerAdd() {
      this.visible = true
    },
    handlerDelete() {
      deleteTags(this.$refs.xTagTable.getCheckboxRecords()).then(res => {
        this.handlerSubmit()
        this.$message.success('删除成功')
      })
    },
    handlerInsert() {
      let that = this
      this.$refs.xInsertAdd.validate(function(error) {
        if(!error) {
          insertTags([that.addForm]).then(res=> {
            that.visible = false
            that.$message.success("插入成功")
            that.handlerSubmit()
          })
        }
      })
    },
    handleCancel() {
      this.visible = false
    }
  }
}
</script>

<style lang="less" scoped>
.main-div {
  margin: -4px -10px 0 3px;
}

.params-panel {
  padding: 6px 12px;
  background: #FFFFFF;
  border-radius: 4px;
  margin-bottom: 6px;
}
</style>