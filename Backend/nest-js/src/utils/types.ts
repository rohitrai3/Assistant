export type ConversationRequest = {
  id: string;
  data: Float32Array;
};

export type ContentBlockStart = {
  type: 'content_block_start';
  index: number;
  content_block: Thinking | Text;
};

export type ContentBlockDelta = {
  type: 'content_block_delta';
  index: number;
  delta: TextDelta | ThinkingDelta | SignatureDelta;
};

export type SignatureDelta = {
  type: 'signature_delta';
  signature: string;
};

export type Text = {
  type: 'text';
  text: string;
};

export type TextDelta = {
  type: 'text_delta';
  text: string;
};

export type Thinking = {
  type: 'thinking';
  thinking: string;
};

export type ThinkingDelta = {
  type: 'thinking_delta';
  thinking: string;
};

export type Message = {
  role: string;
  content: string;
};
