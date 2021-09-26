<template>
  <div class="main-iframe">
    <iframe  :id="id" :src="url" frameborder="0" width="100%" height="100%" scrolling="auto" ></iframe>
  </div>
</template>

<script>
  import { randomUUID } from '@/utils/util'

  export default {
    name: "IframeRQReportView",
    props: ['url'],
    data () {
      return {
        id: randomUUID()
      }
    },
    computed: {
      height() {
        return this.$store.getters.bodyHeight - 100
      }
    }
  }
</script>

<style lang="less" scoped>
.main-iframe {
  background-color: #FFFFFF;
  margin: -12px -20px -12px -6px;
  height: calc(100vh - 91.8px)
}
</style>