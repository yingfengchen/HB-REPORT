<template>
  <g>
    <rect :x="-(height/4)" :y="-(height/10)" :width="height/2" :height="height/5" :fill="color" stroke="#000000" :stroke-width="width" rx="8" ry="8" />
    <text :x="0" :y="(height/12)" fill="#000000" font-family="Cursive" :font-size="30*(height/1450)" font-weight="normal" stroke="#000000" text-anchor="middle" transform="matrix(1 0 0 1 0 0)" xml:space="preserve">{{ eqId }}</text>
  </g>
</template>

<script>
export default {
  name: 'Inline',
  // eslint-disable-next-line vue/require-prop-types
  props: ['height', 'color', 'width', 'eqId']
}
</script>

<style scoped>

</style>
