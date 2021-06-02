<template>
  <span class="full-screen" @click="handleFullScreen">
    <a-icon v-if="!fullScreen" style="font-size: 16px; padding: 4px" type="fullscreen" />
    <a-icon v-if="fullScreen" style="font-size: 16px; padding: 4px" type="fullscreen-exit" />
  </span>
</template>

<script>
export default {
  name: 'Fullscreen',
  computed: {
    fullScreen(){
      return this.$store.getters.fullScreen
    }
  },
  methods: {
    handleFullScreen() {
      this.$bus.$emit('fullscreen')
    }
  }
}
</script>

<style lang="less" scoped>
.full-screen-wrapper {
  top: 50px !important;
}

.full-screen {
  width: 40px;
  display: inline-block;
  transition: all 0.3s;
  text-align: center;

  span {
    vertical-align: initial;
  }
}

.full-screen:hover {
  background: #f1f1f1;
}
</style>