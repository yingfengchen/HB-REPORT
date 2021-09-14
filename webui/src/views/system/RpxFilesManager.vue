<template>
  <a-card :bordered="false" class="main-div">
    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button @click="handleAdd" type="primary" icon="plus">上传文件</a-button>
      <a-button
        @click="batchDel"
        v-if="selectedRowKeys.length > 0"
        ghost
        type="primary"
        icon="delete">批量删除
      </a-button>
    </div>

    <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
      <i class="anticon anticon-info-circle ant-alert-icon"></i>已选择&nbsp;<a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项&nbsp;&nbsp;
    </div>
    <a-table
      size="middle"
      rowKey="file_name"
      :columns="columns"
      :scroll="{x: 1500}"
      :pagination="false"
      :dataSource="datasource"
      :loading="loading"
      :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
    >
      <!-- 字符串超长截取省略号显示 -->
      <span slot="text_ellipsis" slot-scope="text">
          <j-ellipsis :value="text" :length="40" />
        </span>
      <!-- 字符串超长截取省略号显示-->
      <span slot="webPath" slot-scope="text">
        {{ text.indexOf('_arg') > -1 ? '参数文件' : '\{\{ window._CONFIG[\'raqReportUrl\'] \}\}/showReport.jsp?rpx=' + text }}
      </span>
    </a-table>
    <a-modal
      title="报表文件上传"
      :visible="visible"
      @ok="handleOk"
      @cancel="handleCancel"
    >
      <a-upload-dragger
        name="file"
        accept=".rpx,.rpg"
        :action="uploadAction"
        :multiple="true"
        :headers="headers"
      >
        <p class="ant-upload-drag-icon">
          <a-icon type="inbox" />
        </p>
        <p class="ant-upload-text">
          点击上传或者拖拽图片上传
        </p>
        <p class="ant-upload-hint">
          支持单传或者多传，只支持上传rpx，rpg文件
        </p>
      </a-upload-dragger>
    </a-modal>
  </a-card>
</template>

<script>
import { getAction, postAction } from '@api/manage'
import DataTable from '@comp/DataTable'
import Vue from 'vue'
import { ACCESS_TOKEN } from '@/store/mutation-types'

export default {
  name: 'RpxFilesManager',
  components: { DataTable },
  data() {
    return {
      files: null,
      columns: [
        {
          title: '文件名称',
          dataIndex: 'file_name',
          key: 'file_name',
          width: 250,
          scopedSlots: { customRender: 'text_ellipsis' }
        },
        {
          title: '文件大小',
          dataIndex: 'file_size',
          key: 'file_size',
          width: 150
        },
        {
          title: '最后修改时间',
          dataIndex: 'last_modify_time',
          key: 'last_modify_time',
          width: 200
        },
        {
          title: '路径',
          dataIndex: 'path',
          key: 'path',
          scopedSlots: { customRender: 'text_ellipsis' }
        },
        {
          title: '前端路径参数',
          dataIndex: 'file_name',
          key: 'web_path',
          scopedSlots: { customRender: 'webPath' },
          width: 500
        }
      ],
      datasource: [],
      loading: false,
      selectedRowKeys: [],
      selectionRows: [],
      visible: false,
      uploadAction: window._CONFIG['domianURL']+"/sys/common/rpxUpload",
      headers: {}
    }
  },
  created() {
    const token = Vue.ls.get(ACCESS_TOKEN);
    this.headers = {"X-Access-Token":token}
    this.getFilesInfo()
  },
  methods: {
    getFilesInfo() {
      this.loading = true
      getAction('/sys/common/getRpxFileList', {}).then((res) => {
        if (res && res['code'] === 200) {
          this.datasource = res['result']
        } else {
          this.$message.error(res['message'])
        }
        this.loading = false
      }).catch(error => {
        console.log(error)
        this.loading = false
      })
    },
    batchDel() {
      postAction('/sys/common/rpxDelete', { fileNames: this.selectedRowKeys }).then(res => {
        if (res['success']) {
          this.$message.success(res['message'])
        }else{
          this.$message.error(res['message'])
        }
        this.getFilesInfo()
      })
      this.selectedRowKeys = []
    },
    handleAdd() {
      this.visible = true
    },
    onSelectChange(selectedRowKeys, selectionRows) {
      this.selectedRowKeys = selectedRowKeys
      this.selectionRows = selectionRows
    },
    handleOk() {
      this.visible = false
      this.getFilesInfo()
    },
    handleCancel() {
      this.visible = false
    }
  }
}
</script>

<style scoped>
.main-div {
  margin-right: -12px;
}
</style>