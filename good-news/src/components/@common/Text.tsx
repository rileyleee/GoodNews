// 텍스트

import { ReactNode } from "react";
import styled from "styled-components";

interface TextProps {
  size: TextSize;
  color?: TextColor;
  children?: ReactNode;
  className?: string;
  isBold?: boolean;
  onClick?: (event: React.MouseEvent<HTMLSpanElement, MouseEvent>) => void;
}

type TextSize = "text1" | "text2" | "text3" | "text4" | "text5" | "text6";
type TextColor = "White" | "Black" | "Gray";

type TextStyle = {
  fontSize: string;
};
const TextStyles: Record<TextSize, TextStyle> = {
  text1: {
    fontSize: "40px",
  },
  text2: {
    fontSize: "36px",
  },
  text3: {
    fontSize: "32px",
  },
  text4: {
    fontSize: "24px",
  },
  text5: {
    fontSize: "20px",
  },
  text6: {
    fontSize: "16px",
  },
};

const COLOR_STYLES: Record<TextColor, string> = {
  White: "#ffffff",
  Black: "#000000",
  Gray: "#545454",
};
const StyledText = styled.div<TextProps>`
  font-size: ${(props) => TextStyles[props.size].fontSize};
  color: ${(props) => COLOR_STYLES[props.color || "Black"]};
  font-weight: ${(props) => props.isBold === true && 600};
`;

const Text: React.FC<TextProps> = ({
  children,
  size = "text6",
  color = "Black",
  className,
  isBold,
  onClick,
}: TextProps) => {
  return (
    <StyledText
      size={size}
      color={color}
      className={className}
      isBold={isBold}
      onClick={onClick}
    >
      {children}
    </StyledText>
  );
};

export default Text;
