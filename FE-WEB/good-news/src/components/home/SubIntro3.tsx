import styled from "styled-components"
import Text from "../@common/Text"
import Card from "../@common/Card"

// 지도 관련 설명
const SubIntro3 = () => {
    return(
        <StyledMapPageWrapper>
            <StyledMapIntro className="flex flex-row">
                <div className="basis-1/4 flex items-center justify-center">
                    <StyledMapImage className="place-items-center" src="/assets/subIntroImg.png"/>
                </div>
                <div className="basis-3/4 flex flex-col">
                    <div className="flex justify-center align-center">
                        <Text className="mt-20" size="text2"><strong>지도</strong>로 내가 필요한 정보를 한 눈에</Text>
                    </div>
                    <div className="mt-10 mx-12 grid grid-cols-3 gap-10">
                        <Card className="flex drop-shadow-lg justify-center items-center h-24" size="Medium" color="SkyBlue">
                            <StyledFilterImage className="mr-1" src="/assets/mapShelter.png"/>
                            <Text className="ml-1" size="text4">대피소</Text>
                        </Card>
                        <Card className="flex drop-shadow-lg justify-center items-center h-24" size="Medium" color="SkyBlue">
                            <StyledFilterImage className="mr-1" src="/assets/mapHospital.png"/>
                            <Text className="ml-1" size="text4">병원</Text>
                        </Card>
                        <Card className="flex drop-shadow-lg justify-center items-center h-24" size="Medium" color="SkyBlue">
                            <StyledFilterImage className="mr-1" src="/assets/mapMart.png"/>
                            <Text className="ml-1" size="text4">식료품점</Text>
                        </Card>
                        <Card className="flex drop-shadow-lg justify-center items-center h-24" size="Medium" color="SkyBlue">
                            <StyledFilterImage className="mr-1" src="/assets/mapFamily.png"/>
                            <Text className="ml-1" size="text4">가족 위치</Text>
                        </Card>
                        <Card className="flex drop-shadow-lg justify-center items-center h-24" size="Medium" color="SkyBlue">
                            <StyledFilterImage className="mr-1" src="/assets/mapPromise.png"/>
                            <Text className="ml-1" size="text4">약속 장소</Text>
                        </Card>
                        <Card className="flex drop-shadow-lg justify-center items-center h-24" size="Medium" color="SkyBlue">
                            <StyledFilterImage className="mr-1" src="/assets/mapAroundPerson.png"/>
                            <Text className="ml-1" size="text4">주변 사람 위치</Text>
                        </Card>
                    </div>
                    <div className="mt-24 mr-12">
                        <Text className="text-right" size="text4">재난 상황에 맞는 대피소를 확인할 수 있고</Text>
                        <Text className="text-right" size="text4">사용자의 위험 정보 공유를 통해</Text>
                        <Text className="text-right" size="text4">대피소 현황을 업데이트하여 피해를 최소화할 수 있어요</Text>
                    </div>
                </div>
            </StyledMapIntro>
        </StyledMapPageWrapper>
    )
}

export default SubIntro3

const StyledMapPageWrapper = styled.div`
  width: 100%;
  height: calc(100vh - 60px);
  display: flex;
  justify-content: center;
  align-items: center;
`

const StyledMapIntro = styled.div`
  width: 80%;
  height: 100%;
  margin: 0 auto;
`;

const StyledMapImage = styled.img`
    height: 85%;
`
const StyledFilterImage = styled.img`
    width: 60px;
    height: 60px;
`