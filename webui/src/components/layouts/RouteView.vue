<template>
  <div class="main">
    <keep-alive :include="includedComponents">
      <router-view v-if="keepAlive&&judgeTypeOfReport" />
    </keep-alive>
    <router-view v-if="!keepAlive&&judgeTypeOfReport" />
    <iframe-r-q-report-view
      v-for="item in rqReport"
      :key="item"
      v-show="$route.meta.url === item"
      :url="item"
    />
  </div>
</template>

<script>
import Vue from 'vue'
import { CACHE_INCLUDED_ROUTES } from '@/store/mutation-types'
import IframeRQReportView from '@comp/layouts/IframeRQReportView'

export default {
  name: 'RouteView',
  components: { IframeRQReportView },
  data() {
    return {
      rqReport: []
    }
  },
  computed: {
    //update-begin--Author:scott  Date:20201015 for：路由缓存问题，关闭了tab页时再打开就不刷新 #842
    includedComponents() {
      const includedRouters = Vue.ls.get(CACHE_INCLUDED_ROUTES)
      console.log('includedRouters：' + includedRouters)

      //如果是缓存路由，则加入到 cache_included_routes
      if (this.$route.meta.keepAlive && this.$route.meta.componentName) {
        let cacheRouterArray = Vue.ls.get(CACHE_INCLUDED_ROUTES) || []
        if (!cacheRouterArray.includes(this.$route.meta.componentName)) {
          cacheRouterArray.push(this.$route.meta.componentName)
          // cacheRouterArray.push("OnlCgformHeadList")
          console.log('Vue ls set componentName ：' + this.$route.meta.componentName)
          Vue.ls.set(CACHE_INCLUDED_ROUTES, cacheRouterArray)
          console.log('Vue ls includedRouterArrays ：' + Vue.ls.get(CACHE_INCLUDED_ROUTES))
          return cacheRouterArray
        }
      }
      return includedRouters
    },
    //update-end--Author:scott  Date:20201015 for：路由缓存问题，关闭了tab页时再打开就不刷新 #842
    keepAlive() {
      return this.$route.meta.keepAlive
    },
    judgeTypeOfReport() {
      let r_url = this.$route.meta.url
      if(r_url&&this.rqReport.indexOf(r_url) === -1){
        this.rqReport.push(r_url)
      }
      return r_url === undefined
    }
  }
}
</script>