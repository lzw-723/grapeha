<script setup>
import {ref, watch, defineProps} from "vue";

const props = defineProps({
  cover: String,
  title: String,
  author: String,
})
</script>

<template>
  <div class="book">
    <var-card
      :src="props.cover"
      :title="props.title"
      :subtitle="props.author"
    />
  </div>
</template>

<style scoped>
.book {
  width: 200px;
  height: 300px;
}
</style>
