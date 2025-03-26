<script setup>
import {onMounted, useTemplateRef, watch, ref, computed} from "vue";
import "@shoelace-style/shoelace/dist/components/dialog/dialog.js";

const props = defineProps({
  src: {
    type: String,
    required: true,
  }
})

const emit = defineEmits(["next", "goto"])

const shadow = useTemplateRef("shadow");
const html = ref("");

const bookBg = ref("bg");
const bookFont = ref("font-serif");
const bookStyle = computed(() => ({
  "book": true,
  "font-serif": bookFont.value === "font-serif",
  "font-sans-serif": bookFont.value === "font-sans-serif",
  "font-monospace": bookFont.value === "font-monospace",
  "bg": bookBg.value === "bg",
  "bg-white": bookBg.value === "bg-white",
  "bg-black": bookBg.value === "bg-black",
  "bg-yellow": bookBg.value === "bg-yellow",
}));

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
    emit("goto", e.target.attributes.href.value);
  }
}


</script>

<template>
  <div ref="shadow"></div>
  <div class="control-bar">
    <sl-button-group>
      背景色:
      <sl-button @click="bookBg='bg'">默认</sl-button>
      <sl-button @click="bookBg='bg-white'">白</sl-button>
      <sl-button @click="bookBg='bg-black'">黑</sl-button>
      <sl-button @click="bookBg='bg-yellow'">黄</sl-button>
    </sl-button-group>
    <sl-button-group>
      字体:
      <sl-button @click="bookFont='font-serif'">
        衬线
      </sl-button>
      <sl-button @click="bookFont='font-sans-serif'">
        无衬线
      </sl-button>
      <sl-button @click="bookFont='font-monospace'">
        等宽
      </sl-button>
    </sl-button-group>
  </div>
  <sl-dialog label="大图查看" :open="showImage" @sl-after-hide="showImage = false">
    <img :src="img" alt="大图查看" @click="showImage = false">
  </sl-dialog>
  <div :class="bookStyle" v-html="html" @click="clickHandler"></div>
  <sl-button @click="$emit('next')">下一章</sl-button>
</template>

<style scoped>
.control-bar {
  text-align: center;
  position: sticky;
  left: 0;
  top: 0;
}

.book {
  width: 90vw;
  margin: 1rem;
}

.book:deep(img) {
  max-width: 80vw;
  height: auto;
}

.bg {
  background-color: beige;
}

.bg-white {
  background-color: white;
}

.bg-black {
  background-color: black;
  color: white;
}

.bg-yellow {
  background-color: cornsilk;
}

.font-serif {
  font-family: Georgia, 'Times New Roman', "宋体", serif;
}

.font-sans-serif {
  font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "微软雅黑", Arial, sans-serif;
}

.font-monospace {
  font-family: Consolas, Menlo, Monaco, "Andale Mono", "Ubuntu Mono", monospace;
}
</style>
