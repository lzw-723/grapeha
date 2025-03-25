<script setup>
import "@shoelace-style/shoelace/dist/components/card/card.js"

const props = defineProps({
  cover: String,
  title: String,
  author: String,
})
</script>

<template>
  <sl-card>
    <img
      slot="image"
      :src="cover"
      class="card-image"
      loading="lazy"
      decoding="async"
      alt="cover"
    />
    <span class="card-title">{{ props.title }}</span>
  </sl-card>
</template>

<style scoped>
sl-card {
  width: 140px;
}

.card-image {
  width: 100%;
  height: 210px;
  object-fit: cover;
}

.card-title {
  display: -webkit-box;
  -webkit-line-clamp: 2; /* 限制显示行数 */
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis; /* 添加省略号 */
  white-space: normal; /* 允许换行 */
}
</style>
