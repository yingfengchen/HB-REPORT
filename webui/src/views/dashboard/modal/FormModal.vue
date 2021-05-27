<template>
  <a-modal
    width="880px"
    :title="title"
    :visible="visible"
    @ok="handlerOK"
    @cancel="handlerCancel"
  >
    <a-row type="flex" justify="end">
      <a-button v-print="'#formModalArea'" icon="printer" />
    </a-row>
    <div class="main-div" id="formModalArea">
      <div style="text-align: center; margin-bottom: 15px;">
        <h2 style="font-weight: bolder">{{ title }}</h2>
      </div>
      <a-form-model>
        <a-row v-for="row in formJson" :key="row.key">
          <a-col v-for="col in row.children" :key="row.key + row.children.indexOf(col)" :span="col.span"
                 :style="{height: (col.height || 32) +'px'}">
            <form-item :type="col.type" v-model="col.value" :value="col.value" :disabled="col.disabled"></form-item>
          </a-col>
        </a-row>
      </a-form-model>
    </div>
  </a-modal>
</template>

<script>
import FormItem from '@views/dashboard/components/FormItem'
import { getCurrentTime } from '@/utils/util'

export default {
  name: 'FormModal',
  components: { FormItem },
  props: {
    title: {
      type: String,
      default: '快捷操作'
    },
    visible: {
      type: Boolean,
      default: false
    },
    formJson: {
      type: Array,
      default: []
    }
  },
  methods: {
    handlerOK() {
      console.log(this.formJson)
      this.$emit('close', null)
      this.$message.success('已提交申请，请移至 OA 查看结果')
    },
    handlerCancel() {
      this.$emit('close', null)
    }
  }
}
</script>

<style lang="less" scoped>
.main-div {
  /deep/ .ant-row {
    border: 1px solid;
    margin-bottom: -1px !important;

    .ant-col:last-child {
      border: none;
    }

    .ant-col {
      margin-bottom: -1px !important;
      border-bottom: 1px solid;
      border-right: 1px solid;
      line-height: 32px;
    }
  }
}
</style>