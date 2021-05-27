<template>
  <div class="main-item">
    <label v-if="type==='label'">{{ value }}</label>
    <a-input v-if="type==='input'" v-model="model" :disabled="disabled" @change="handleChange" />
    <a-input v-if="type==='textarea'" type="textarea" v-model="model" :disabled="disabled" @change="handleChange" />
    <a-range-picker show-time v-if="type==='rangePicker'" v-model="model" :style="{width: '100%'}" @change="handleChange" />
  </div>
</template>

<script>
export default {
  name: 'FormItem',
  model: {
    prop: 'model',
    event: 'changed'
  },
  props: {
    type: {
      type: String,
      default: 'label'
    },
    value: {
      default: null
    },
    disabled: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      model: null
    }
  },
  mounted() {
    this.model = this.value
  },
  methods: {
    handleChange(val) {
      let value = val['target'] ? val['target']['value'] : (val['value'] || val)
      this.$emit('changed', value)
    }
  }
}
</script>

<style lang="less" scoped>
.main-item {
  width: 100%;
  height: 100%;
  text-align: center;

  /deep/ input {
    border: 0;
    color: black;
  }

  /deep/ span {
    border: 0;
  }

  /deep/ textarea {
    border: 0;
    color: black;
  }
}
</style>