CellularAutomataConfigurationBuilder configBuilder = new CellularAutomataConfigurationBuilder();
CellularAutomataConfiguration config = configBuilder.setHeight(50)
                                                    .setWidth(50)
                                                    .setTotalIterations(10)
                                                    .setStatusList(/* a List */)
                                                    .setNeighborhoodType(NeighborhoodType.MOORE)
                                                    .setDefaultStatus(new DefaultStatus("key", "value"))
                                                    /** ... */
                                                    .build();