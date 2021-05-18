<template>
  <vxe-select
    v-model='model'
    placeholder='请选择'
    clearable
    style='width: 100%'
    @clear='handleClear'
    @change='handleChange'
  >
    <vxe-option
      v-for='option in optionsList'
      :key='option[optionConfig.value]'
      :value='option[optionConfig.value]'
      :label='option[optionConfig.label]'
    />
  </vxe-select>
</template>

<script>
import { httpAction } from '@/api/manage'

export default {
  name: 'QuerySelect',
  model: {
    prop: 'model',
    event: 'changed'
  },
  // eslint-disable-next-line vue/require-prop-types
  props: ['optionConfig', 'url', 'params', 'options', 'method'],
  data() {
    return {
      optionsList: [],
      model: '',
      params_real: {}
    }
  },
  watch: {
    'params': {
      handler(n, o) {
        this.params_real = n
        this.queryResult()
      },
      deep: true
    }
  },
  created() {
    this.params_real = this.params
    this.queryResult()
  },
  methods: {
    queryResult() {
      let that = this
      if (this.url) {
        httpAction(this.url, this.params_real, this.method||'get').then(res => {
          console.log(res)
          if (res.result && res.result.records) {
            that.optionsList = res.result.records
          } else {
            that.optionsList = res.result
          }
        })
      } else {
        this.optionsList = this.options
      }
    },
    handleChange(val) {
      if (val['value']) {
        this.$emit('changed', val['value'])
        this.$emit('change', val['value'])
      }
    },
    handleClear() {
      this.$emit('changed', '')
    },
    clear() {
      this.model = ''
      this.optionsList = []
      this.queryResult()
    }
  }
}
</script>

<style scoped>

</style>
