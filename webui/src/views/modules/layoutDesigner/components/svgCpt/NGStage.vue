<template>
  <g>
    <rect :x="-(height/2)" :y="-(height/2.6)" :width="height" :height="height/1.3" :fill="color" stroke="#357d91" :stroke-width="width" />
  </g>
</template>

<script>
export default {
  name: 'NGStage',
  // eslint-disable-next-line vue/require-prop-types
  props: ['height', 'color', 'width']
}
</script>

<style scoped>

</style>
