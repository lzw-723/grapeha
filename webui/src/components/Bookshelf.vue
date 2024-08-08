<script setup>
const props = defineProps({
  title: String,
});
</script>

<template>
  <div>
    <div
      class="bookshelf var-elevation--2"
      v-ripple>{{ props.title }}
    </div>
  </div>
</template>

<style scoped>
.bookshelf {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 368px;
  height: 260px;
  cursor: pointer;
  transition: background-color 0.25s;
  background-color: var(--color-surface-container-highest);
}
</style>
