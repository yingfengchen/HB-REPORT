<template>
  <div class="info-overview">
    <label>{{ label }}</label>
  </div>
</template>

<script>
export default {
  name: 'InfoView',
  props: ['label']
}
</script>

<style lang="less" scoped>
.info-overview {
  width: 100%;
  height: 39px;
  margin: 10px 0 10px 0;
  background: #e7f4ff;
  border-radius: 6px;
  border: 1px solid #76c4ff;
  padding-left: 15px;
  line-height: 39px;
  font-weight: bolder;
}
</style>