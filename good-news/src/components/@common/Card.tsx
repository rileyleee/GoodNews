// 감싸는 wrap (카드 컴포넌트)

import React, { ReactNode } from "react";
import styled from "styled-components";

interface CardProps {
  size: CardSize;
  color: CardColor;
  children: ReactNode;
  className?: string;
}

type CardSize = "ExtraLarge" | "Large" | "Medium" | "Small";
type CardColor = "White" | "SkyBlue";

type CardStyle = {
  radius: string;
};

const CardStyles: Record<CardSize, CardStyle> = {
  ExtraLarge: {
    radius: "30px",
  },
  Large: {
    radius: "28px",
  },
  Medium: {
    radius: "16px",
  },
  Small: {
    radius: "30px",
  },
};

const COLOR_STYLES: Record<CardColor, string> = {
  White: "#FFFFFF",
  SkyBlue: "#E7ECEF",
};

const StyledCard = styled.div<CardProps>`
  border-radius: ${(props) => CardStyles[props.size].radius};
  color: ${(props) => COLOR_STYLES[props.color]};
`;

const Card = ({ size, color, children, className }: CardProps) => {
  return (
    <StyledCard size={size} color={color} className={className}>
      {children}
    </StyledCard>
  );
};

export default Card;
