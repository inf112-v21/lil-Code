package objects;

public class flag implements IObject{
    int posX;
    int posY;

    public flag(int x, int y){
        setXPosition(x);
        setYPosition(y);
    }

    @Override
    public void setXPosition(int x) {
        posX = x;
    }

    @Override
    public void setYPosition(int y) {
        posY = y;
    }

    @Override
    public int getX() {
        return posX;
    }

    @Override
    public int getY() {
        return posY;
    }
}
