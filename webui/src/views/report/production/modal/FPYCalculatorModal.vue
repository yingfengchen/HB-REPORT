<template>
  <a-modal
    title="投入数量计算"
    class="main-modal"
    :visible="visible"
    @ok="() => { this.$emit('close', null) }"
    @cancel="() => { this.$emit('close', null) }"
  >
    <info-view label="允许返工：所有不良均可返工、直接报废：不进行任何返工" />
    <a-row>
      <a-col class="form-body" :span="24">
        <a-radio-group v-model="mode" @change="handlerModeChange">
          <a-radio value="R"> 允许返工 </a-radio>
          <a-radio value="S"> 直接报废 </a-radio>
        </a-radio-group>
      </a-col>
      <a-col :span="12">当前 Flow 直通率</a-col>
      <a-col class="form-body" :span="12">
        <a>{{ fpy }} %</a>
      </a-col>
      <a-col :span="12">产出数量设定</a-col>
      <a-col class="form-body" :span="12">
        <a-input-number :step="1" v-model="outputCount" @change="handlerChange" />
      </a-col>
      <a-col :span="12">需要投入数量</a-col>
      <a-col class="form-body" :span="12">
        <h3 style="color: #1b801b">{{ inputCount }}</h3>
      </a-col>
    </a-row>
  </a-modal>
</template>

<script>
import InfoView from '@views/report/components/InfoView'
export default {
  name: 'FPYCalculatorModal',
  components: { InfoView },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    fpy: {
      type: Number,
      default: 100
    }
  },
  data() {
    return {
      inputCount: 0,
      outputCount: 0,
      mode: 'R'
    }
  },
  methods: {
    handlerChange(data) {
      switch (this.mode){
        case 'R': this.inputCount = Math.ceil(data * (1 + (1 - (this.fpy/100)))); break;
        case 'S': this.inputCount = Math.ceil(data * (1 / (this.fpy/100))); break;
        default: break;
      }
    },
    handlerModeChange() {
      this.handlerChange(this.outputCount)
    }
  }
}
</script>

<style lang="less" scoped>
.main-modal {
  /deep/ .ant-col{
    margin-bottom: 20px;
  }
  .form-body {
    text-align: center;
  }
}
</style>