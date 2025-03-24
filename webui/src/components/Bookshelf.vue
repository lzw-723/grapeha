<script setup>
import "@shoelace-style/shoelace/dist/components/card/card.js"
const props = defineProps({
  title: String,
});
</script>

<template>
  <sl-card>
    {{ props.title }}
  </sl-card>
</template>

<style scoped>

</style>
