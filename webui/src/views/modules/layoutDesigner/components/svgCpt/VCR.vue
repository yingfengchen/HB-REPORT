<template>
  <g>
    <circle :cx="0" :cy="0" :fill="color" stroke="#eeeeee" :stroke-width="width" :r="(height/8)" />
  </g>
</template>

<script>
export default {
  name: 'VCR',
  // eslint-disable-next-line vue/require-prop-types
  props: ['height', 'color', 'width']
}
</script>

<style scoped>

</style>
