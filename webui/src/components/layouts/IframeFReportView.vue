<template>
  <div class="main-iframe">
    <iframe  :id="id" :src="url" frameborder="0" width="100%" height="100%" scrolling="auto" ></iframe>
  </div>
</template>

<script>
  export default {
    name: "IframeFReportView",
    data () {
      return {
        url: "",
        id:""
      }
    },
    computed: {
      height() {
        return this.$store.getters.bodyHeight - 100
      }
    },
    created () {
      this.goUrl()
    },
    updated () {
      this.goUrl()
    },
    watch: {
      $route(to, from) {
        this.goUrl();
      }
    },
    methods: {
      goUrl () {
        let url = this.$route.meta.url
        let id = this.$route.path
        this.id = id
        //url = "http://www.baidu.com"
        console.log("------url------"+url)
        if (url !== null && url !== undefined) {
          this.url = url;
          //window.open(this.url);
        }
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