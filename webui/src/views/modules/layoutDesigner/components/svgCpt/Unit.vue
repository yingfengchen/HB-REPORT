<template>
  <g>
    <rect :x="-(height/2)" :y="-(height/3)" :width="height" :height="height/1.5" :fill="color" :stroke="strokeColor" :stroke-width="width" rx="8" ry="8" />
    <text :x="0" :y="(height/3.5)" :font-size="72*(height/1400)" stroke="#000000" text-anchor="middle" transform="matrix(1 0 0 1 0 0)" xml:space="preserve">{{ text || 'TEXT' }}</text>
  </g>
</template>

<script>
export default {
  name: 'Unit',
  // eslint-disable-next-line vue/require-prop-types
  props: ['height', 'color', 'width', 'text', 'strokeColor']
}
</script>

<style scoped>

</style>
