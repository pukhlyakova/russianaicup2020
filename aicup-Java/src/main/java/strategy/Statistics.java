package strategy;

public class Statistics {
    private int populationUse = 0;
    private int populationProvide = 0;

    public void increasePopulationUse(int val) {
        populationUse += val;
    }

    public int getPopulationUse() {
        return populationUse;
    }

    public void increasePopulationProvide(int val) {
        populationProvide += val;
    }

    public int getPopulationProvide() {
        return populationProvide;
    }

    public boolean shouldBuildHouse() {
        return populationProvide - populationUse <= 5;
    }
}
