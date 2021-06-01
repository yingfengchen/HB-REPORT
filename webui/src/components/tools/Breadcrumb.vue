<template>
  <a-breadcrumb class="breadcrumb">
    <a-breadcrumb-item v-for="(item, index) in breadList" :key="index">
      <span v-if="item.name != name">
        {{ generateTitleBreadcrumb(item.meta.title) }}
      </span>
      <span v-else>{{ generateTitleBreadcrumb(item.meta.title) }}</span>
    </a-breadcrumb-item>
  </a-breadcrumb>
</template>

<script>
import { generateTitle } from '@/utils/i18n'

export default {
  data() {
    return {
      name: '',
      breadList: [],
    }
  },
  created() {
    this.getBreadcrumb()
  },
  methods: {
    getBreadcrumb() {

      console.log('this.$route.matched', this.$route.matched)

      this.breadList = []

      this.name = this.$route.name
      this.$route.matched.forEach((item) => {
        // item.meta.name === 'dashboard' ? item.path = '/dashboard' : this.$route.path === item.path
        this.breadList.push(item)
      })
    },
    generateTitleBreadcrumb(title){
      return generateTitle(title)
    }
  },
  watch: {
    $route() {
      this.getBreadcrumb()
    }
  }
}
</script>

<style lang="less" scoped>
.breadcrumb {
  display: inline-block;
}
</style>