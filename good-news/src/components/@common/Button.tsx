// 버튼 컴포넌트
import React, { ReactNode } from "react";
import styled from "styled-components";

interface ButtonProps {
  size: ButtonSize;
  color: ButtonColor;
  onClick?: () => void;
  children: ReactNode;
  className?: string;
  isActive?: Boolean;
}

type ButtonSize = "Large" | "Medium" | "Small";
type ButtonColor = "Main" | "Sub" | "Safe" | "Danger" | "Undefined";

type ButtonStyle = {
  height: string;
  fontSize: string;
};

const ButtonStyles: Record<ButtonSize, ButtonStyle> = {
  Large: {
    height: "48px",
    fontSize: "20px",
  },
  Medium: {
    height: "44px",
    fontSize: "16px",
  },
  Small: {
    height: "36px",
    fontSize: "16px",
  },
};

const COLOR_STYLES: Record<ButtonColor, string> = {
  Main: "#274C77",
  Sub: "#C0D6DF",
  Safe: "#34B476",
  Danger: "#EF476F",
  Undefined: "#C5C5C5",
};

const StyledButton = styled.button<ButtonProps>`
  height: ${(props) => ButtonStyles[props.size].height};
  font-size: ${(props) => ButtonStyles[props.size].fontSize};
  border-radius: 8px;
  background-color: ${(props) =>
    props.isActive ? COLOR_STYLES[props.color] : "white"};
  border: 1px solid ${(props) => COLOR_STYLES[props.color]};
  color: ${(props) => (props.isActive ? "white" : COLOR_STYLES[props.color])};
  &:hover {
    filter: brightness(0.9);
  }
`;

const Button = ({
  size,
  onClick,
  children,
  color,
  className,
  isActive,
}: ButtonProps) => {
  return (
    <StyledButton
      size={size}
      onClick={onClick}
      color={color}
      className={className}
      isActive={isActive}
    >
      {children}
    </StyledButton>
  );
};

export default Button;
