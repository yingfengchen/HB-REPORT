<template>
  <div class="main-table">
    <vxe-grid
      ref="xGrid"
      stripe
      auto-resize
      show-footer-overflow
      show-overflow
      highlight-current-row
      v-bind="gridOptions"
      :height="height"
      :data="loadingDataSource"
      :cell-style="cellStyle"
      :toolbar-config="{
        size: 'small',
        enabled: !isHideToolBar,
        zoom: false,
        custom: !isHideBasicTool,
        import: canImport,
        export: false,
        print: !isHideBasicTool,
        refresh: false,
        slots: {
          buttons: 'toolbar_buttons',
          tools: 'toolbat_custom'
        },
      }"
      :sort-config="sortConfig"
      :edit-config="editConfig"
      :edit-rules="editRules"
      @cell-click="handleCellClick"
    >
      <template #toolbar_buttons>
        <a-icon type="appstore" :style="{ marginLeft: '5px' }"/>
        <label :style="{ marginLeft: '5px' }">{{ title }}</label>
        <slot name="header-buttons" />
      </template>
      <template #toolbat_custom>
        <vxe-button v-if="canExport" style="margin-right: 12px" icon="vxe-icon--download" circle @click="handleExportAll" />
      </template>
      <template v-slot:pager>
        <vxe-pager
          v-if="!isHidePager"
          background
          :layouts="totalOnly? ['Total']:['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'FullJump', 'Total', 'Sizes']"
          :current-page.sync="pagerConfig.currentPage"
          :page-size.sync="pagerConfig.pageSize"
          :page-sizes="[10, 15, 20, 50, 100, 500]"
          :total="pagerConfig.total"
          @page-change="handlePageChange"
        />
      </template>
    </vxe-grid>
  </div>
</template>

<script>
import * as arrayMethods from 'xe-utils'
import { getAction } from '@api/manage'

export default {
  name: 'DataTable',
  // eslint-disable-next-line vue/require-prop-types
  props: ['datasource', 'columns', 'height', 'rowId', 'totalOnly', 'canImport', 'canExport', 'isHideToolBar', 'isHideBasicTool', 'cellStyle', 'title', 'pageSize', 'pageRemote', 'pageRequestUrl', 'pageParams', 'isHidePager', 'sortConfig', 'editConfig', 'editRules'],
  data() {
    return {
      pagerConfig: {
        currentPage: 1,
        pageSize: this.pageSize || 50,
        total: 0
      },
      gridOptions: {
        border: true,
        resizable: true,
        showHeaderOverflow: true,
        showOverflow: true,
        highlightHoverRow: true,
        keepSource: true,
        id: 'full_edit_1',
        rowId: this.rowId || 'id',
        customConfig: {
          storage: true
        },
        printConfig: {},
        sortConfig: {
          trigger: 'cell'
        },
        filterConfig: {
          remote: true
        },
        toolbarConfig: {
          slots: {
            buttons: 'toolbar_buttons'
          },
          refresh: false,
          import: this.import,
          export: this.export,
          print: true,
          zoom: true,
          custom: true
        },
        columns: this.columns,
        importConfig: {
          types: ['csv'],
          modes: ['insert']
        },
        exportConfig: {
          types: ['csv', 'xlsx'],
          modes: ['current', 'selected']
        },
        checkboxConfig: {
          reserve: true,
          highlight: true,
          range: true
        },
        editRules: {},
        editConfig: {
          trigger: 'click',
          mode: 'row',
          showStatus: true
        }
      },
      loadingDataSource: []
    }
  },
  watch: {
    datasource: function(newVal) {
      const that = this
      if (newVal == null) {
        this.loadingDataSource = null
        return
      }
      if(this.totalOnly || this.isHidePager){
        that.loadingDataSource = newVal
      }else {
        that.loadingDataSource = arrayMethods.slice(newVal, (that.pagerConfig.currentPage - 1) * that.pagerConfig.pageSize, that.pagerConfig.currentPage * that.pagerConfig.pageSize)
      }
      that.pagerConfig.total = newVal.length
    }
  },
  methods: {
    checkColumnMethod({ column }) {
      return !['nickname', 'role'].includes(column.property)
    },
    loadData() {
      if (this.pageRemote) {
        this.handlePageChange({ currentPage: 1, pageSize: this.pagerConfig.pageSize })
      }
    },
    handlePageChange({ currentPage, pageSize }) {
      const that = this
      that.pagerConfig.currentPage = currentPage
      that.pagerConfig.pageSize = pageSize

      if (this.pageRemote) {
        this.pageParams['skip'] = ((currentPage - 1) * pageSize)
        this.pageParams['maxCount'] = pageSize
        getAction(
          this.pageRequestUrl,
          this.pageParams
        ).then(res => {
          that.pagerConfig.total = res.totalCount
          that.loadingDataSource = res.items
        })
      } else {
        let end = 0
        if (currentPage * pageSize > that.datasource.length) {
          end = that.datasource.length
        } else {
          end = currentPage * pageSize
        }
        that.loadingDataSource = arrayMethods.slice(that.datasource, (currentPage - 1) * pageSize, end)
      }
    },
    handleCellClick({ row }) {
      this.$emit('cell-click', row)
    },
    handleExportAll() {
      this.$refs.xGrid.exportData({
        filename: this.title,
        type: 'xlsx',
        isHeader: true,
        isFooter: true,
        data: this.datasource
      })
    },
    exportTableData() {
      this.$refs.xGrid.openExport({
        original: true
      })
    },
    importData() {
      return this.$refs.xGrid.importData()
    },
    getTableData() {
      return this.$refs.xGrid.getTableData()['fullData']
    },
    getCheckboxRecords() {
      return this.$refs.xGrid.getCheckboxRecords()
    }
  }
}
</script>

<style lang="less" scoped>
.main-table {
  margin-top: 3px;

  /deep/ .vxe-header--column {
    height: 32px;
  }

  /deep/ .vxe-table--render-default .vxe-body--column.col--ellipsis, .vxe-table--render-default.vxe-editable .vxe-body--column, .vxe-table--render-default .vxe-footer--column.col--ellipsis, .vxe-table--render-default .vxe-header--column.col--ellipsis {
    height: 32px;
  }

  /deep/ .vxe-toolbar {
    background: #f3f3f3;
  }

  /deep/ .vxe-toolbar.size--small {
    height: 35px;
  }

  /deep/ .vxe-pager {
    height: 35px;
  }
}
</style>
