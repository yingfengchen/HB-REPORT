<template>
  <g>
    <rect :x="-(height/2)" :y="-(height/2.6)" :width="height" :height="height/1.3" :fill="color" stroke="#000000" :stroke-width="width" />
    <text :x="0" :y="(height/3)" :font-size="72*(height/1400)" stroke="#000000" text-anchor="middle" transform="matrix(1 0 0 1 0 0)" xml:space="preserve">{{ text || 'TEXT' }}</text>
  </g>
</template>

<script>
export default {
  name: 'SubUnit',
  // eslint-disable-next-line vue/require-prop-types
  props: ['height', 'color', 'width', 'text']
}
</script>

<style scoped>

</style>
