<script setup>
import {defineProps, onMounted, useTemplateRef, watch, ref} from "vue";
import "@shoelace-style/shoelace/dist/components/dialog/dialog.js";

const props = defineProps({
  src: {
    type: String,
    required: true,
  }
})

const shadow = useTemplateRef("shadow");
const html = ref("");

const showImage = ref(false);

const img = ref();

onMounted(() => {
  shadow.value.attachShadow({mode: "open"});
})

watch(() => props.src, () => {
  console.log("src changed");
  fetch(props.src).then(r => r.text()).then(r => {
    html.value = r;
  })
})

function clickHandler(e) {
  if (e.target.tagName === "IMG") {
    img.value = e.target.src;
    showImage.value = true;
  } else if (e.target.tagName === "image") {
    img.value = e.target.getAttribute("xlink:href");
    showImage.value = true;
  } else if (e.target.tagName === "A") {
    e.preventDefault();
    console.log(e.target.attributes.href.value);
  }
}


</script>

<template>
  <div ref="shadow"></div>
  <sl-dialog label="大图查看" :open="showImage" @sl-after-hide="showImage = false">
    <img :src="img" alt="大图查看" @click="showImage = false">
  </sl-dialog>
  <div class="book" v-html="html" @click="clickHandler"></div>
</template>

<style scoped>
.book {
  width: 90vw;
  margin: 1rem;
}

.book >>> img {
  max-width: 80vw;
  height: auto;
}
</style>
