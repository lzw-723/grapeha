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
      alt="A kitten walks towards camera on top of pallet."
    />
    <div slot="header">
      {{ props.title}}
      <sl-icon-button name="gear" label="Settings"></sl-icon-button>
    </div>
      {{ props.author}}
  </sl-card>
</template>

<style scoped>
.card-image {
  width: 100%;
  height: 300px;
  object-fit: cover;
}
</style>
