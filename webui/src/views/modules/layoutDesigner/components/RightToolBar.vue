<template>
  <a-tabs class="components-layout-right">
    <a-tab-pane key="1" tab="外观">
      <a-form layout="horizontal" :label-col="{ span: 7 }" :wrapper-col="{ span: 16 }" label-align="left">
        <a-form-item label="id">
          <p>{{ svgInfo.id }}</p>
        </a-form-item>
        <a-form-item label="类型">
          <p>{{ selectSvgInfo.type }}</p>
        </a-form-item>
        <a-form-item v-if="svgInfo" label="名称">
          <p>{{ selectSvgInfo.title }}</p>
        </a-form-item>
        <a-form-item v-if="svgInfo" label="设备名">
          <a-input style="width: 100%" v-model:value="selectSvgInfo.class" placeholder="请输入组件class" @pressEnter="handlerAddClass"/>
        </a-form-item>
        <a-form-item v-if="svgInfo" label="X轴坐标">
          <a-row style="width: 100%">
            <a-col :span="16">
              <a-slider v-model:value="selectSvgInfo.svgPositionX" :min="0" :max="1920"/>
            </a-col>
            <a-col :span="7">
              <a-input-number style="width: 100%" v-model:value="selectSvgInfo.svgPositionX" :min="0" :max="1920" />
            </a-col>
          </a-row>
        </a-form-item>
        <a-form-item v-if="svgInfo" label="Y轴坐标">
          <a-row style="width: 100%">
            <a-col :span="16">
              <a-slider v-model:value="selectSvgInfo.svgPositionY" :min="0" :max="1080"/>
            </a-col>
            <a-col :span="7">
              <a-input-number style="width: 100%" v-model:value="selectSvgInfo.svgPositionY" :min="0" :max="1080" />
            </a-col>
          </a-row>
        </a-form-item>
        <a-form-item v-if="svgInfo" label="颜色">
          <input style="width: 100%" type="color" v-model="selectSvgInfo.svgColor">
        </a-form-item>
        <a-form-item v-if="svgInfo" label="位置">
          <a-radio-group :value="selectSvgInfo['zIndex']" @change="handlezIndexChange">
            <a-radio-button :value="0">
              置底
            </a-radio-button>
            <a-radio-button :value="1">
              居中
            </a-radio-button>
            <a-radio-button :value="2">
              置顶
            </a-radio-button>
          </a-radio-group>
        </a-form-item>
        <a-form-item v-if="svgInfo" label="大小">
          <a-row style="width: 100%">
            <a-col :span="16">
              <a-slider v-model:value="selectSvgInfo.height" :min="10" :max="800"/>
            </a-col>
            <a-col :span="7">
              <a-input-number style="width: 100%" v-model:value="selectSvgInfo.height" :min="10" :max="800" />
            </a-col>
          </a-row>
        </a-form-item>
        <a-form-item v-if="svgInfo" label="描边宽度">
          <a-row style="width: 100%">
            <a-col :span="16">
              <a-slider v-model:value="selectSvgInfo.width" :min="1" :max="12"/>
            </a-col>
            <a-col :span="7">
              <a-input-number style="width: 100%" v-model:value="selectSvgInfo.width" :min="1" :max="12"/>
            </a-col>
          </a-row>
        </a-form-item>
        <a-form-item v-if="svgInfo" label="描边颜色">
          <input type="color" style="width: 100%" v-model="selectSvgInfo.strokeColor">
        </a-form-item>
        <a-form-item v-if="svgInfo" label="旋转">
          <a-row style="width: 100%">
            <a-col :span="16">
              <a-slider v-model:value="selectSvgInfo.angle" :min="0" :max="360"/>
            </a-col>
            <a-col :span="7">
              <a-input-number style="width: 100%" v-model:value="selectSvgInfo.angle" :min="0" :max="360"/>
            </a-col>
          </a-row>
        </a-form-item>
        <a-form-item v-if="svgInfo" label="文本">
          <a-input style="width: 100%" v-model:value="selectSvgInfo.svgText" placeholder="请输入文本"/>
        </a-form-item>
        <a-form-item v-if="svgInfo" label="字体大小">
          <a-row style="width: 100%">
            <a-col :span="16">
              <a-slider v-model="selectSvgInfo.fontSize" :min="11" :max="30"/>
            </a-col>
            <a-col :span="7">
              <a-input-number style="width: 100%" v-model:value="selectSvgInfo.fontSize" :min="11" :max="30"/>
            </a-col>
          </a-row>
        </a-form-item>
      </a-form>
    </a-tab-pane>
    <a-tab-pane key="2" tab="数据">
      <div class="bg-code">{{ selectSvgInfo }}</div>
    </a-tab-pane>
  </a-tabs>

</template>
<script>
export default {
  props: ['svgInfo'],
  computed: {
    selectSvgInfo() {
      return this.svgInfo;
    }
  },
  watch: {
    'selectSvgInfo.tableRowCount': function (newVal) {
      this.$emit('setTableAttr', this.selectSvgInfo.id, newVal, this.selectSvgInfo.tableColCount)
    },
    'selectSvgInfo.tableColCount': function (newVal) {
      this.$emit('setTableAttr', this.selectSvgInfo.id, this.selectSvgInfo.tableRowCount, newVal)
    }
  },
  methods: {
    handlerAddClass() {
      document.getElementById(this.svgInfo.id).classList.add(this.svgInfo.class)
      this.$message.success("修改成功")
    },
    handlezIndexChange(e) {
      this.selectSvgInfo['zIndex'] = e.target.value;
      this.$emit('zIndexChange', e.target.value)
    }
  }
};
</script>

<style scoped lang="less">
.components-layout-right {
  margin-left: 10px;

  .ant-tabs-bar {
    margin-bottom: 0 !important;
  }

  .ant-form-item-label {
    width: 100px !important;
    flex-shrink: 0;
  }

  .ant-input-number, input {
    width: 160px;
    border-radius: 4px;
    border-color: #ddd;
  }

  .ant-form-item-control {
    line-height: 24px !important;

    p {
      margin-bottom: 0 !important;
    }


  }

  .ant-form-item {
    display: flex;
    align-items: center;
    flex-flow: nowrap;
    position: relative;
    margin-bottom: 0;
    padding: 8px 0;

    &:after {
      content: "";
      position: absolute;
      bottom: 0;
      left: 15px;
      right: 15px;
      bottom: 0;
      height: 1px;
      background: #f2f2f2;
    }

    &:last-child::after {
      content: unset
    }
  }
}

.btn-sure {
  width: 260px;
  margin: 20px;
}

.bg-code {
  background: #444;
  color: #fff;
  width: calc(100% - 20px);
  margin: 20px 10px;
  border-radius: 4px;
  padding: 10px;
}
</style>
