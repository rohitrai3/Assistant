export type ContentBlockStart = {
  type: "content_block_start";
  index: number;
  contentBlock: ThinkingBlock | TextBlock;
}

export type ContentBlockDelta = {
  type: "content_block_delta";
  index: number;
  delta: ThinkingDelta | TextDelta;
}

export type TextDelta = {
  type: "text_delta";
  text: string;
}

export type TextBlock = {
  type: "text";
  text: string;
}

export type ThinkingDelta = {
  type: "thinking_delta";
  thinking: string;
}

export type ThinkingBlock = {
  type: "thinking";
  thinking: string;
}

